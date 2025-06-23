package com.example.m_commerce_admin.features.products.domain.usecase

import com.example.m_commerce_admin.core.shared.components.usecase.UseCase
import com.example.m_commerce_admin.features.products.domain.entity.DomainProductInput
import com.example.m_commerce_admin.features.products.domain.repository.ProductRepository
import javax.inject.Inject

data class AddProductWithImagesParams(
    val product: DomainProductInput,
    val imageUris: List<String>
)

class AddProductWithImagesUseCase @Inject constructor(
    private val repo: ProductRepository
) : UseCase<AddProductWithImagesParams, Result<Unit>> {

    override suspend fun invoke(params: AddProductWithImagesParams): Result<Unit> {
        return repo.addProductWithImages(params.product, params.imageUris)
    }

} 