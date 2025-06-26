package com.example.m_commerce_admin.features.products.domain.usecase

import com.example.m_commerce_admin.core.shared.components.usecase.UseCase
import com.example.m_commerce_admin.features.products.domain.entity.RestProduct
import com.example.m_commerce_admin.features.products.domain.entity.RestProductInput
import com.example.m_commerce_admin.features.products.domain.repository.RestProductRepository
import javax.inject.Inject

class CreateRestProductUseCase @Inject constructor(
    private val repository: RestProductRepository
) : UseCase<RestProductInput, Result<RestProduct>> {

    override suspend fun invoke(params: RestProductInput): Result<RestProduct> {
        return repository.createProduct(params)
    }
} 