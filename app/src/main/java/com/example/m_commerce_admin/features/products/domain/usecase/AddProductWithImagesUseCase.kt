package com.example.m_commerce_admin.features.products.domain.usecase

import android.content.Context
import android.net.Uri
import com.example.m_commerce_admin.core.shared.components.usecase.UseCase
import com.example.m_commerce_admin.features.products.domain.entity.DomainProductInput
import com.example.m_commerce_admin.features.products.domain.repository.ProductRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AddProductWithImagesUseCase @Inject constructor(
    private val repository: ProductRepository,
    @ApplicationContext context: Context
) : UseCase<AddProductWithImagesParams, Result<Unit>> {

    override suspend fun invoke(params: AddProductWithImagesParams): Result<Unit> {
        return repository.uploadImagesAndAddProduct(
            product = params.product,
            imageUris = params.imageUris,
            context = params.context
        )
    }
}
data class AddProductWithImagesParams(
    val product: DomainProductInput,
    val imageUris: List<Uri>,
    val context: Context
)
