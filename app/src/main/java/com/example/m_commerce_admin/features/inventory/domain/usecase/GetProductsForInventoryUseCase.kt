package com.example.m_commerce_admin.features.inventory.domain.usecase

import com.example.m_commerce_admin.core.shared.components.usecase.UseCase
import com.example.m_commerce_admin.features.inventory.domain.usecase.params.GetProductsForInventoryParams
import com.example.m_commerce_admin.features.products.domain.entity.rest.RestProduct
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

