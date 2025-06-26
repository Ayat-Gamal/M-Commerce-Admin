package com.example.m_commerce_admin.features.products.domain.repository

import android.content.Context
import android.net.Uri
import com.example.m_commerce_admin.features.products.domain.entity.*
import kotlinx.coroutines.flow.Flow

interface RestProductRepository {
    fun getAllProducts(limit: Int, pageInfo: String?, status: String?): Flow<Result<List<RestProduct>>>
    suspend fun getProductById(productId: Long): Result<RestProduct>
    suspend fun createProduct(product: RestProductInput): Result<RestProduct>
    suspend fun updateProduct(productId: Long, product: RestProductUpdateInput): Result<RestProduct>
    suspend fun deleteProduct(productId: Long): Result<Unit>
    
    suspend fun uploadImagesAndAddProduct(product: RestProductInput, imageUris: List<Uri>, context: Context): Result<RestProduct>
    
    // Asset operations
    suspend fun uploadImageToAssets(themeId: Long, imageUri: Uri, context: Context): Result<String>
    suspend fun addImagesToProduct(productId: Long, imageUrls: List<String>): Result<RestProduct>
    
    suspend fun setInventoryLevel(locationId: Long, inventoryItemId: Long, available: Int): Result<Unit>
} 