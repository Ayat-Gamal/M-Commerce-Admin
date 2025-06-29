package com.example.m_commerce_admin.features.products.data.retrofitRemote

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.example.m_commerce_admin.PublishProductMutation
import com.example.m_commerce_admin.StagedUploadsCreateMutation
import com.example.m_commerce_admin.features.products.data.mapper.graph.toGraphQL
import com.example.m_commerce_admin.features.products.domain.entity.StagedUploadInput
import com.example.m_commerce_admin.features.products.domain.entity.StagedUploadTarget
import com.example.m_commerce_admin.type.StagedUploadTargetGenerateUploadResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RetrofitProductDataSourceImpl @Inject constructor(
    private val api: ShopifyProductApi,
    private val apolloClient: ApolloClient
) : RetrofitProductDataSource {

    override fun getAllProducts(
        limit: Int,
        pageInfo: String?,
        status: String?
    ): Flow<Result<List<ProductDto>>> = flow {
        try {
            emit(Result.success(api.getAllProducts(limit, pageInfo, status).products))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getProductById(productId: Long): Result<ProductDto> {
        return try {
            Result.success(api.getProductById(productId).product)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createProduct(product: ProductCreateDto): Result<ProductDto> {
        return try {
            Result.success(api.createProduct(CreateProductRequest(product)).product)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateProduct(
        productId: Long,
        product: ProductUpdateDto
    ): Result<ProductDto> {
        return try {
            Result.success(api.updateProduct(productId, UpdateProductRequest(product)).product)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun uploadAsset(themeId: Long, asset: AssetCreateDto): Result<AssetDto> {
        return try {
            val request = AssetUploadRequest(asset)
            val response = api.uploadAsset(themeId, request)
            Result.success(response.asset)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAssets(themeId: Long): Result<List<AssetDto>> {
        return try {
            val response = api.getAssets(themeId)
            Result.success(response.assets)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun deleteProduct(productId: Long): Result<Unit> {
        return try {
            api.deleteProduct(productId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun publishProduct(productId: Long) {

        val MAD_STORE = "gid://shopify/Publication/159563710713"
        val id = "gid://shopify/Product/${productId}"
        apolloClient.mutation(PublishProductMutation(id, publicationId = MAD_STORE))
            .execute()

        Log.i("TAG", "publishProduct: CALLLLEDD")
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

            val okHttpClient = provideOkHttpClient()
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


    override suspend fun setInventoryLevel(
        inventoryItemId: Long,
        locationId: Long,
        available: Int
    ): Result<Unit> {
        return try {
            delay(2000) // Optional safety delay, keep if needed

            val response = api.setInventoryLevel(
                SetInventoryLevelRequest(
                    available = available,
                    locationId = locationId,
                    inventoryItemId = inventoryItemId
                )
            )

            // ✅ Fix: response is a data class, not a Response<T>
            if (response == null) {
                throw Exception("Set inventory failed: response.level is null")
            }

            Log.d("DEBUG", "setInventoryLevel: ")
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }






    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header("User-Agent", "MCommerceAdmin/1.0")
                    .header("Accept", "*/*")
                chain.proceed(requestBuilder.build())
            }
            .build()
    }
}
