package com.example.m_commerce_admin.features.products.domain.usecase

import com.example.m_commerce_admin.core.shared.components.usecase.UseCase
import com.example.m_commerce_admin.features.products.domain.entity.rest.RestProduct
import com.example.m_commerce_admin.features.products.domain.repository.RestProductRepository
import javax.inject.Inject

class PublishProductUseCase @Inject constructor(
    private val repository: RestProductRepository
) : UseCase<Long, Unit> {

    override suspend fun invoke(params: Long) {
        return repository.publishProduct(params)
    }
}