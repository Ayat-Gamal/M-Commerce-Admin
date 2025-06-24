package com.example.m_commerce_admin.features.products.data.remote

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.apollographql.apollo.exception.ApolloException
import com.example.m_commerce_admin.AddProductMutation
import com.example.m_commerce_admin.AddProductWithImagesMutation
import com.example.m_commerce_admin.GetProductsQuery
import com.example.m_commerce_admin.StagedUploadsCreateMutation
import com.example.m_commerce_admin.features.products.data.mapper.toDomain
import com.example.m_commerce_admin.features.products.data.mapper.toGraphQL
import com.example.m_commerce_admin.features.products.data.model.StagedUploadInput
import com.example.m_commerce_admin.features.products.data.model.StagedUploadTarget
import com.example.m_commerce_admin.features.products.domain.entity.DomainProductInput
import com.example.m_commerce_admin.features.products.presentation.states.GetProductState
import com.example.m_commerce_admin.type.CreateMediaInput
import com.example.m_commerce_admin.type.MediaContentType
import com.example.m_commerce_admin.type.ProductInput
import com.example.m_commerce_admin.type.StagedUploadTargetGenerateUploadResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okio.BufferedSink
import javax.inject.Inject

class ProductRemoteDataSourceImpl @Inject constructor(
    private val apolloClient: ApolloClient,
    private val okHttpClient: OkHttpClient

) : ProductRemoteDataSource {

    override fun getProducts(first: Int, after: String?): Flow<GetProductState> = flow {
        try {
            Log.d("ProductRemoteDataSource", "Starting getProducts: first=$first, after=$after")
            emit(GetProductState.Loading)

            Log.d("ProductRemoteDataSource", "Executing GraphQL query...")
            val response = apolloClient.query(
                GetProductsQuery(first = first, after = Optional.presentIfNotNull(after))
            ).execute()
            if (response.hasErrors()) {
                val errorMessage = response.errors?.firstOrNull()?.message ?: "GraphQL error occurred"
                Log.e("ProductRemoteDataSource", "GraphQL errors: ${response.errors}")
                emit(GetProductState.Error(errorMessage))
                return@flow
            }

            if (response.data == null) {
                Log.e("ProductRemoteDataSource", "Response data is null")
                emit(GetProductState.Error("No data received from server"))
                return@flow
            }

            val products = response.data?.products?.edges?.map { edge ->
                edge.node.toDomain()
            } ?: emptyList()

            val hasNextPage = response.data?.products?.pageInfo?.hasNextPage ?: false
            val endCursor = response.data?.products?.pageInfo?.endCursor

            Log.d("ProductRemoteDataSource", "Products loaded: ${products.size}, hasNext: $hasNextPage, cursor: $endCursor")
            emit(GetProductState.Success(products, hasNextPage, endCursor))
        } catch (e: ApolloException) {
            Log.e("ProductRemoteDataSource", "ApolloException in getProducts", e)
            emit(GetProductState.Error("GraphQL error: ${e.message}"))
        } catch (e: Exception) {
            Log.e("ProductRemoteDataSource", "Exception in getProducts", e)
            emit(GetProductState.Error(e.message ?: "Network error occurred"))
        }
    }

    override suspend fun addProduct(product: ProductInput): Result<Unit> {
        return try {
            val response = apolloClient.mutation(AddProductMutation(product)).execute()

            response.data?.productCreate?.userErrors?.let { errors ->
                if (errors.isNotEmpty()) {
                    return Result.failure(Exception(errors.first().message))
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun prepareStagedUploadInputs(
        context: Context,
        imageUris: List<Uri>
    ): List<StagedUploadInput> {
        return imageUris.map { uri ->
            val fileName = context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                cursor.moveToFirst()
                cursor.getString(nameIndex)
            } ?: uri.lastPathSegment ?: "image_${System.currentTimeMillis()}.jpg"

            val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"

            StagedUploadInput(
                fileName = fileName,
                mimeType = mimeType,
                resource =StagedUploadTargetGenerateUploadResource.IMAGE// Shopify expects this for image uploads
            )
        }
    }

    override suspend fun requestStagedUploads(
        inputs: List<StagedUploadInput>
    ): List<StagedUploadTarget> {
        val gqlInputs = inputs.map { it.toGraphQL() }
        val response = apolloClient.mutation(StagedUploadsCreateMutation(gqlInputs)).execute()

        return response.data?.stagedUploadsCreate?.stagedTargets?.map { target ->
            StagedUploadTarget(
                url = target.url.toString(),
                resourceUrl = target.resourceUrl.toString(),
                parameters = target.parameters.associate { param ->
                    param.name to param.value
                }
            )
        } ?: throw Exception("Failed to get staged upload targets")
    }
    suspend fun uploadImageToStagedTarget(
        context: Context,
        uri: Uri,
        target: StagedUploadTarget
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(uri) ?: return@withContext false
            val imageBytes = inputStream.readBytes()
            inputStream.close()

            // 1. Get accurate file metadata
            val mimeType = contentResolver.getType(uri) ?: "image/jpeg"
            val fileName = target.parameters["key"]?.substringAfterLast('/')
                ?: uri.lastPathSegment
                ?: "upload_${System.currentTimeMillis()}.jpg"

            // 2. Build the multipart request with EXACT parameter order
            val requestBodyBuilder = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .apply {
                    // Add parameters in EXACT order received from Shopify
                    target.parameters.entries.sortedBy { it.key }.forEach { (key, value) ->
                        addFormDataPart(key, value)
                        Log.d("UploadParam", "Added param: $key=$value")
                    }

                    // File part MUST be last
                    addFormDataPart(
                        "file",
                        fileName,
                        RequestBody.create(mimeType.toMediaTypeOrNull(), imageBytes)
                    )
                }

            // 3. Create and execute request
            val request = Request.Builder()
                .url(target.url)
                .header("Host", "shopify-staged-uploads.storage.googleapis.com")
                .post(requestBodyBuilder.build())
                .build()

            val response = okHttpClient.newCall(request).execute()

            if (!response.isSuccessful) {
                val errorBody = response.body?.string()
                Log.e("Upload", "❌ Failed: ${response.code} - $errorBody")
                return@withContext false
            }

            Log.d("Upload", "✅ Upload success")
            true
        } catch (e: Exception) {
            Log.e("Upload", "❌ Exception", e)
            false
        }
    }


    override suspend fun addProductWithMedia(
        product: ProductInput,
        media: List<CreateMediaInput>
    ): Result<Unit> {
        return try {
            val response = apolloClient.mutation(
                AddProductWithImagesMutation(
                    input = product,
                    media = Optional.Present(media)
                )
            ).execute()
            Log.d("AddProductWithMedia", "Mutation Data: ${response.data}")
            Log.d("AddProductWithMedia", "UserErrors: ${response.data?.productCreate?.userErrors}")

            if (response.hasErrors()) {
                val errorMessage = response.errors?.firstOrNull()?.message ?: "Unknown error"
                return Result.failure(Exception(errorMessage))
            }

            response.data?.productCreate?.userErrors?.let { errors ->
                if (errors.isNotEmpty()) {
                    return Result.failure(Exception(errors.first().message))
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}




