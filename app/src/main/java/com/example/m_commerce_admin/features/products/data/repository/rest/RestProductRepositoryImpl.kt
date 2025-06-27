package com.example.m_commerce_admin.features.products.data.repository.rest

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Base64
import android.util.Log
import com.apollographql.apollo.api.Optional
import com.example.m_commerce_admin.config.constant.ShopifyConfig
import com.example.m_commerce_admin.features.products.data.mapper.toGraphQL
import com.example.m_commerce_admin.features.products.data.mapper.toProductCreateDto
import com.example.m_commerce_admin.features.products.data.mapper.toProductUpdateDto
import com.example.m_commerce_admin.features.products.data.mapper.toRestProduct
import com.example.m_commerce_admin.features.products.data.retrofitRemote.*
import com.example.m_commerce_admin.features.products.domain.entity.DomainProductInput
import com.example.m_commerce_admin.features.products.domain.entity.StagedUploadTarget
import com.example.m_commerce_admin.features.products.domain.entity.rest.RestProduct
import com.example.m_commerce_admin.features.products.domain.entity.rest.RestProductImageInput
import com.example.m_commerce_admin.features.products.domain.entity.rest.RestProductInput
import com.example.m_commerce_admin.features.products.domain.entity.rest.RestProductUpdateInput
import com.example.m_commerce_admin.features.products.domain.repository.RestProductRepository
import com.example.m_commerce_admin.type.CreateMediaInput
import com.example.m_commerce_admin.type.MediaContentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import javax.inject.Inject
import kotlin.math.log

class RestProductRepositoryImpl @Inject constructor(
    private val retrofitDataSource: RetrofitProductDataSource,
    private val okHttpClient: OkHttpClient,
 ) : RestProductRepository {

    override fun getAllProducts(
        limit: Int,
        pageInfo: String?,
        status: String?
    ): Flow<Result<List<RestProduct>>> {
        return retrofitDataSource.getAllProducts(limit, pageInfo, status)
            .map { result ->
                result.map { productDtos ->
                    productDtos.map { it.toRestProduct() }
                }
            }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun getProductById(productId: Long): Result<RestProduct> {
        return retrofitDataSource.getProductById(productId)
            .map { it.toRestProduct() }
    }

    override suspend fun createProduct(product: RestProductInput): Result<RestProduct> {
        return retrofitDataSource.createProduct(product.toProductCreateDto())
            .map { it.toRestProduct() }

    }

    override suspend fun updateProduct(
        productId: Long,
        product: RestProductUpdateInput
    ): Result<RestProduct> {
        return retrofitDataSource.updateProduct(productId, product.toProductUpdateDto())
            .map { it.toRestProduct() }
    }



    override suspend fun uploadImagesAndAddProduct(
        product: RestProductInput,
        imageUris: List<Uri>,
        context: Context
    ): Result<RestProduct> = runCatching {
        // Step 1: Create product via REST (no images)
        val productWithoutImages = product.copy(images = null)
        val createResult = createProduct(productWithoutImages)
        if (createResult.isFailure) throw createResult.exceptionOrNull()
            ?: Exception("Product creation failed")

        val createdProduct = createResult.getOrThrow()

        // Step 2: Prepare staged uploads
        if (imageUris.isNotEmpty()) {
            val inputs = retrofitDataSource.prepareStagedUploadInputs(context, imageUris)
            val targets = retrofitDataSource.requestStagedUploads(inputs)

            // Step 3: Upload images to Shopify CDN
            imageUris.zip(targets).forEach { (uri, target) ->
                val uploadSuccess =
                    retrofitDataSource.uploadImageToStagedTarget(context, uri, target)
                if (!uploadSuccess) throw Exception("Failed to upload image: ${uri.lastPathSegment}")
            }

            // Step 4: Attach CDN images to product via REST
            val imageUrls = targets.map { it.resourceUrl }

            val addImagesResult = addImagesToProduct(createdProduct.id, imageUrls)
            if (addImagesResult.isFailure) throw addImagesResult.exceptionOrNull()
                ?: Exception("Failed to add images to product")
        }

        createdProduct
    }.fold(
        onSuccess = { Result.success(it) },
        onFailure = { Result.failure(it) }
    )

    private suspend fun uploadFileToTarget(
        context: Context,
        uri: Uri,
        target: StagedUploadTarget
    ): Boolean {
        return retrofitDataSource.uploadImageToStagedTarget(context, uri, target)
    }


    override suspend fun uploadImageToAssets(
        themeId: Long,
        imageUri: Uri,
        context: Context
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(imageUri) ?: 
                return@withContext Result.failure(Exception("Cannot open image stream"))
            
            val imageBytes = inputStream.readBytes()
            inputStream.close()
            
            if (imageBytes.isEmpty()) {
                return@withContext Result.failure(Exception("Image is empty"))
            }
            
            // Convert to base64
            val base64Image = Base64.encodeToString(imageBytes, Base64.NO_WRAP)
            
            // Generate unique key for the asset
            val fileName = getFileName(context, imageUri)
            val key = "assets/${System.currentTimeMillis()}_$fileName"
            
            // Get mime type
            val mimeType = contentResolver.getType(imageUri) ?: "image/jpeg"
            
            // Create asset
            val asset = AssetCreateDto(
                key = key,
                attachment = "data:$mimeType;base64,$base64Image",
                contentType = mimeType
            )
            
            val uploadResult = retrofitDataSource.uploadAsset(themeId, asset)
            
            uploadResult.fold(
                onSuccess = { assetDto ->
                    val publicUrl = assetDto.publicUrl ?: 
                        "https://cdn.shopify.com/s/files/1/0000/0000/themes/${themeId}/assets/${assetDto.key}"
                    Result.success(publicUrl)
                },
                onFailure = { error ->
                    Result.failure(error)
                }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun getFileName(context: Context, uri: Uri): String {
        return context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            cursor.getString(nameIndex)
        } ?: uri.lastPathSegment ?: "image_${System.currentTimeMillis()}.jpg"
    }
    override suspend fun addImagesToProduct(
        productId: Long,
        imageUrls: List<String>
    ): Result<RestProduct> {
        return try {
            val imageInputs = imageUrls.mapIndexed { index, url ->
                RestProductImageInput(
                    src = url,
                    position = index
                )
            }
            
            val updateInput = RestProductUpdateInput(images = imageInputs)
            updateProduct(productId, updateInput)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun setInventoryLevel(
        locationId: Long,
        inventoryItemId: Long,
        available: Int
    ): Result<Unit> {
        return try {
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun deleteProduct(productId: Long): Result<Unit> {
        return retrofitDataSource.deleteProduct(productId)
    }
    override suspend fun publishProduct(productId: Long) {
        retrofitDataSource.publishProduct(productId)
    }


} 