package com.example.m_commerce_admin.features.products.domain.usecase.rest

import com.example.m_commerce_admin.core.shared.components.usecase.UseCase
import com.example.m_commerce_admin.features.products.domain.entity.rest.RestProduct
import com.example.m_commerce_admin.features.products.domain.repository.RestProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllRestProductsUseCase @Inject constructor(
    private val repository: RestProductRepository
) : UseCase<GetAllRestProductsParams, Flow<Result<List<RestProduct>>>> {

    override suspend fun invoke(params: GetAllRestProductsParams): Flow<Result<List<RestProduct>>> {
        return repository.getAllProducts(params.limit, params.pageInfo, params.status)
    }
}

data class GetAllRestProductsParams(
    val limit: Int = 50,
    val pageInfo: String? = null,
    val status: String? = null
) 