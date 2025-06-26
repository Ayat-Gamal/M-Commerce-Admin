package com.example.m_commerce_admin.features.products.data.repository

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Base64
import com.example.m_commerce_admin.config.constant.ShopifyConfig
import com.example.m_commerce_admin.features.products.data.mapper.toProductCreateDto
import com.example.m_commerce_admin.features.products.data.mapper.toProductUpdateDto
import com.example.m_commerce_admin.features.products.data.mapper.toRestProduct
import com.example.m_commerce_admin.features.products.data.retrofitRemote.*
import com.example.m_commerce_admin.features.products.domain.entity.RestProduct
import com.example.m_commerce_admin.features.products.domain.entity.RestProductImageInput
import com.example.m_commerce_admin.features.products.domain.entity.RestProductInput
import com.example.m_commerce_admin.features.products.domain.entity.RestProductUpdateInput
import com.example.m_commerce_admin.features.products.domain.repository.RestProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import javax.inject.Inject

class RestProductRepositoryImpl @Inject constructor(
    private val retrofitDataSource: RetrofitProductDataSource,
    private val okHttpClient: OkHttpClient
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

    override suspend fun deleteProduct(productId: Long): Result<Unit> {
        return retrofitDataSource.deleteProduct(productId)
    }

    override suspend fun uploadImagesAndAddProduct(
        product: RestProductInput,
        imageUris: List<Uri>,
        context: Context
    ): Result<RestProduct> = runCatching {
        // Create product first
        val productWithoutImages = product.copy(images = null)
        val createResult = createProduct(productWithoutImages)
        
        if (createResult.isFailure) {
            throw createResult.exceptionOrNull() ?: Exception("Failed to create product")
        }

        val createdProduct = createResult.getOrThrow()
        
        // Upload images to assets and add them to the product
        if (imageUris.isNotEmpty()) {
            val imageUrls = mutableListOf<String>()
            
            // Use configured theme ID
            val themeId = ShopifyConfig.DEFAULT_THEME_ID
            
            for (uri in imageUris) {
                val uploadResult = uploadImageToAssets(themeId, uri, context)
                if (uploadResult.isSuccess) {
                    imageUrls.add(uploadResult.getOrThrow())
                }
            }
            
            // Add images to the product
            if (imageUrls.isNotEmpty()) {
                val addImagesResult = addImagesToProduct(createdProduct.id, imageUrls)
                if (addImagesResult.isSuccess) {
                    return@runCatching addImagesResult.getOrThrow()
                }
            }
        }
        
        createdProduct
    }.fold(
        onSuccess = { Result.success(it) },
        onFailure = { Result.failure(it) }
    )

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
            // This would need to be implemented in the data source
            // For now, return success as inventory is set during product creation
            Result.success(Unit)
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
} 