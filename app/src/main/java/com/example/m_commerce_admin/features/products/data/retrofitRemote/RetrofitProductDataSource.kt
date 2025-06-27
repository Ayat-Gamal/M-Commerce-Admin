package com.example.m_commerce_admin.features.products.data.retrofitRemote

import android.content.Context
import android.net.Uri
import com.example.m_commerce_admin.features.products.data.retrofitRemote.*
import com.example.m_commerce_admin.features.products.domain.entity.StagedUploadInput
import com.example.m_commerce_admin.features.products.domain.entity.StagedUploadTarget
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface RetrofitProductDataSource {
    fun getAllProducts(limit: Int, pageInfo: String?, status: String?): Flow<Result<List<ProductDto>>>
    suspend fun getProductById(productId: Long): Result<ProductDto>
    suspend fun createProduct(product: ProductCreateDto): Result<ProductDto>
    suspend fun updateProduct(productId: Long, product: ProductUpdateDto): Result<ProductDto>
    suspend fun deleteProduct(productId: Long): Result<Unit>
    
    // Asset operations
    suspend fun uploadAsset(themeId: Long, asset: AssetCreateDto): Result<AssetDto>
    suspend fun getAssets(themeId: Long): Result<List<AssetDto>>

    suspend fun publishProduct(productId:Long)
    suspend fun prepareStagedUploadInputs(context: Context, imageUris: List<Uri>): List<StagedUploadInput>
    suspend fun requestStagedUploads(inputs: List<StagedUploadInput>): List<StagedUploadTarget>
    suspend fun uploadImageToStagedTarget(context: Context, uri: Uri, target: StagedUploadTarget): Boolean

}

