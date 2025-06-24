package com.example.m_commerce_admin.features.products.domain.usecase

import com.example.m_commerce_admin.core.shared.components.usecase.UseCase
 import com.example.m_commerce_admin.features.products.domain.repository.ProductRepository
import com.example.m_commerce_admin.type.ProductInput
import javax.inject.Inject

class AddProductUseCase @Inject constructor(
    private val repo: ProductRepository
) : UseCase<ProductInput, Result<Unit>> {

    override suspend fun invoke(params: ProductInput): Result<Unit> {
        return repo.addProduct(params)
    }

}
