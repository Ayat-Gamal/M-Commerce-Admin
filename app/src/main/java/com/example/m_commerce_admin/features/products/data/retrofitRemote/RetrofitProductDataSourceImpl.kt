package com.example.m_commerce_admin.features.products.data.retrofitRemote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class RetrofitProductDataSourceImpl @Inject constructor(
    private val api: ShopifyProductApi
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

    override suspend fun updateProduct(productId: Long, product: ProductUpdateDto): Result<ProductDto> {
        return try {
            Result.success(api.updateProduct(productId, UpdateProductRequest(product)).product)
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
}
