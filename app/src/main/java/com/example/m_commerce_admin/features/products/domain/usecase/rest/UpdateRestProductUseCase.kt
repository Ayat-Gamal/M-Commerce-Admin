package com.example.m_commerce_admin.features.products.domain.usecase.rest

import com.example.m_commerce_admin.core.shared.components.usecase.UseCase
import com.example.m_commerce_admin.features.products.domain.entity.rest.RestProduct
import com.example.m_commerce_admin.features.products.domain.repository.RestProductRepository
import com.example.m_commerce_admin.features.products.domain.usecase.params.UpdateRestProductParams
import javax.inject.Inject

class UpdateRestProductUseCase @Inject constructor(
    private val repository: RestProductRepository
) : UseCase<UpdateRestProductParams, Result<RestProduct>> {

    override suspend fun invoke(params: UpdateRestProductParams): Result<RestProduct> {
        return repository.updateProduct(params.productId, params.product)
    }
}
