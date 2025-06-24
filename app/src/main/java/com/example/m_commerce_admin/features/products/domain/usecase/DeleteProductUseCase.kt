package com.example.m_commerce_admin.features.products.domain.usecase

import com.example.m_commerce_admin.core.shared.components.usecase.UseCase
import com.example.m_commerce_admin.features.products.domain.repository.ProductRepository
import javax.inject.Inject

class DeleteProductUseCase @Inject constructor(
    private val repo: ProductRepository
) : UseCase<String, Result<Unit>> {
    override suspend fun invoke(params: String): Result<Unit> {
        return repo.deleteProduct(params)
    }
}
