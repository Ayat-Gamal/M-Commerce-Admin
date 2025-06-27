package com.example.m_commerce_admin.features.products.domain.usecase.rest

import com.example.m_commerce_admin.core.shared.components.usecase.UseCase
import com.example.m_commerce_admin.features.products.domain.repository.RestProductRepository
import javax.inject.Inject

class DeleteRestProductUseCase @Inject constructor(
    private val repository: RestProductRepository
) : UseCase<Long, Result<Unit>> {

    override suspend fun invoke(params: Long): Result<Unit> {
        return repository.deleteProduct(params)
    }
} 