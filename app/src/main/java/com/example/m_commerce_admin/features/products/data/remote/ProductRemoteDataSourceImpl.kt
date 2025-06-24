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
            
            Log.d("StagedUpload", "📁 Preparing upload for: $fileName (MIME: $mimeType, URI: $uri)")

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
            
            // Check for GraphQL errors
            if (response.hasErrors()) {
                val errorMessage = response.errors?.firstOrNull()?.message ?: "Unknown GraphQL error"
                Log.e("StagedUpload", "GraphQL Error: $errorMessage")
                throw Exception("GraphQL Error: $errorMessage")
            }
            
            // Check for user errors
            response.data?.stagedUploadsCreate?.userErrors?.let { errors ->
                if (errors.isNotEmpty()) {
                    val errorMessage = errors.first().message
                    Log.e("StagedUpload", "User Error: $errorMessage")
                    throw Exception("User Error: $errorMessage")
                }
            }
            
            val targets = response.data?.stagedUploadsCreate?.stagedTargets
            if (targets.isNullOrEmpty()) {
                throw Exception("No staged upload targets received from Shopify")
            }
            
            Log.d("StagedUpload", "Successfully received ${targets.size} upload targets")
            
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
            Log.e("StagedUpload", "Failed to request staged uploads", e)
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
                Log.e("Upload", "❌ Cannot open input stream for URI: $uri")
                return@withContext false
            }
            
            val imageBytes = inputStream.readBytes()
            inputStream.close()
            
            if (imageBytes.isEmpty()) {
                Log.e("Upload", "❌ Image bytes are empty for URI: $uri")
                return@withContext false
            }
            
            // Check file size (Shopify has limits, typically 20MB for images)
            val fileSizeMB = imageBytes.size / (1024 * 1024.0)
            if (fileSizeMB > 20) {
                Log.e("Upload", "❌ File too large: ${String.format("%.2f", fileSizeMB)}MB (max 20MB)")
                return@withContext false
            }

            // Get accurate file metadata
            val mimeType = contentResolver.getType(uri) ?: "image/jpeg"
            val fileName = target.parameters["key"]?.substringAfterLast('/')
                ?: uri.lastPathSegment
                ?: "upload_${System.currentTimeMillis()}.jpg"

            Log.d("Upload", "📁 Uploading file: $fileName (${String.format("%.2f", fileSizeMB)}MB, $mimeType)")
            Log.d("Upload", "🎯 Target URL: ${target.url}")
            Log.d("Upload", "🔗 Resource URL: ${target.resourceUrl}")
            Log.d("Upload", "📋 Parameters: ${target.parameters}")

            // For pre-signed URLs, we need to send the file directly as the request body
            // with the correct content type header
            val requestBody = RequestBody.create(mimeType.toMediaTypeOrNull(), imageBytes)

            // Create and execute request with file as direct body
            // Try PUT method which is more common for pre-signed URL uploads
            val request = Request.Builder()
                .url(target.url)
                .put(requestBody)
                .build()

            val response = okHttpClient.newCall(request).execute()

            if (!response.isSuccessful) {
                val errorBody = response.body?.string()
                Log.e("Upload", "❌ Upload failed: ${response.code} - $errorBody")
                Log.e("Upload", "❌ Response headers: ${response.headers}")
                return@withContext false
            }

            Log.d("Upload", "✅ Upload successful for: $fileName")
            true
        } catch (e: Exception) {
            Log.e("Upload", "❌ Exception during upload", e)
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




