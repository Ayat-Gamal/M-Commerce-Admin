package com.example.m_commerce_admin.features.inventory.domain.usecase

import com.example.m_commerce_admin.core.shared.components.usecase.UseCase
import com.example.m_commerce_admin.features.products.domain.entity.RestProduct
import com.example.m_commerce_admin.features.products.domain.repository.RestProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProductsForInventoryUseCase @Inject constructor(
    private val productRepository: RestProductRepository
) : UseCase<GetProductsForInventoryParams, Flow<Result<List<RestProduct>>>> {

    override suspend fun invoke(params: GetProductsForInventoryParams): Flow<Result<List<RestProduct>>> {
        return productRepository.getAllProducts(params.limit, params.pageInfo, params.status)
    }
}

data class GetProductsForInventoryParams(
    val limit: Int = 250,
    val pageInfo: String? = null,
    val status: String? = null
) 