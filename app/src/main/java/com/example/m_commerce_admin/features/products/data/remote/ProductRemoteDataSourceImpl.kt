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
import com.example.m_commerce_admin.DeleteProductMutation
import com.example.m_commerce_admin.GetProductsQuery
import com.example.m_commerce_admin.StagedUploadsCreateMutation
import com.example.m_commerce_admin.features.products.data.mapper.toDomain
import com.example.m_commerce_admin.features.products.data.mapper.toGraphQL
import com.example.m_commerce_admin.features.products.domain.entity.StagedUploadInput
import com.example.m_commerce_admin.features.products.domain.entity.StagedUploadTarget
import com.example.m_commerce_admin.features.products.presentation.states.GetProductState
import com.example.m_commerce_admin.type.CreateMediaInput
import com.example.m_commerce_admin.type.ProductInput
import com.example.m_commerce_admin.type.StagedUploadTargetGenerateUploadResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import javax.inject.Inject

class ProductRemoteDataSourceImpl @Inject constructor(
    private val apolloClient: ApolloClient,
    private val okHttpClient: OkHttpClient

) : ProductRemoteDataSource {

    override fun getProducts(first: Int, after: String?): Flow<GetProductState> = flow {
        try {
            emit(GetProductState.Loading)

            val response = apolloClient.query(
                GetProductsQuery(first = first, after = Optional.presentIfNotNull(after))
            ).execute()
            if (response.hasErrors()) {
                val errorMessage =
                    response.errors?.firstOrNull()?.message ?: "GraphQL error occurred"
                emit(GetProductState.Error(errorMessage))
                return@flow
            }

            if (response.data == null) {
                emit(GetProductState.Error("No data received from server"))
                return@flow
            }

            val products = response.data?.products?.edges?.map { edge ->
                edge.node.toDomain()
            } ?: emptyList()

            val hasNextPage = response.data?.products?.pageInfo?.hasNextPage ?: false
            val endCursor = response.data?.products?.pageInfo?.endCursor

            emit(GetProductState.Success(products, hasNextPage, endCursor))
        } catch (e: ApolloException) {
            emit(GetProductState.Error("GraphQL error: ${e.message}"))
        } catch (e: Exception) {
            emit(GetProductState.Error(e.message ?: "Network error occurred"))
        }
    }

    override suspend fun addProduct(product: ProductInput): Result<Unit> {
        return try {

            val response = apolloClient.mutation(AddProductMutation(product)).execute()

            val gqlInput = ProductInput(
                title = product.title,
                descriptionHtml = product.descriptionHtml,
                productType = product.productType,
                vendor = product.vendor,
                status = product.status
            )

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
            val fileName =
                context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    cursor.moveToFirst()
                    cursor.getString(nameIndex)
                } ?: uri.lastPathSegment ?: "image_${System.currentTimeMillis()}.jpg"

            val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"

            StagedUploadInput(
                fileName = fileName,
                mimeType = mimeType,
                resource = StagedUploadTargetGenerateUploadResource.IMAGE
            )
        }
    }

    override suspend fun requestStagedUploads(
        inputs: List<StagedUploadInput>
    ): List<StagedUploadTarget> {
        try {
            val gqlInputs = inputs.map { it.toGraphQL() }
            val response = apolloClient.mutation(StagedUploadsCreateMutation(gqlInputs)).execute()

            if (response.hasErrors()) {
                val errorMessage =
                    response.errors?.firstOrNull()?.message ?: "Unknown GraphQL error"
                throw Exception("GraphQL Error: $errorMessage")
            }

            // Check for user errors
            response.data?.stagedUploadsCreate?.userErrors?.let { errors ->
                if (errors.isNotEmpty()) {
                    val errorMessage = errors.first().message
                    throw Exception("User Error: $errorMessage")
                }
            }

            val targets = response.data?.stagedUploadsCreate?.stagedTargets
            if (targets.isNullOrEmpty()) {
                throw Exception("No staged upload targets received from Shopify")
            }


            return targets.map { target ->
                StagedUploadTarget(
                    url = target.url.toString(),
                    resourceUrl = target.resourceUrl.toString(),
                    parameters = target.parameters.associate { param ->
                        param.name to param.value
                    }
                )
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun uploadImageToStagedTarget(
        context: Context,
        uri: Uri,
        target: StagedUploadTarget
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(uri) ?: run {
                return@withContext false
            }

            val imageBytes = inputStream.readBytes()
            inputStream.close()

            if (imageBytes.isEmpty()) {
                return@withContext false
            }

            val fileSizeMB = imageBytes.size / (1024 * 1024.0)
            if (fileSizeMB > 20) {
                return@withContext false
            }

            // Get accurate file metadata
            val mimeType = contentResolver.getType(uri) ?: "image/jpeg"
            val fileName = target.parameters["key"]?.substringAfterLast('/')
                ?: uri.lastPathSegment
                ?: "upload_${System.currentTimeMillis()}.jpg"
            val requestBody = RequestBody.create(mimeType.toMediaTypeOrNull(), imageBytes)


            val request = Request.Builder()
                .url(target.url)
                .put(requestBody)
                .build()

            val response = okHttpClient.newCall(request).execute()

            if (!response.isSuccessful) {
                val errorBody = response.body?.string()
                Log.e("Upload", "❌ Upload failed: ${response.code} - $errorBody")
                return@withContext false
            }

            Log.d("Upload", "✅ Upload successful for: $fileName")
            true
        } catch (e: Exception) {
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

    override suspend fun deleteProduct(productId: String): Result<Unit> {
        return try {
            val response = apolloClient.mutation(DeleteProductMutation(productId)).execute()
            response.data?.productDelete?.userErrors?.let { errors ->
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




