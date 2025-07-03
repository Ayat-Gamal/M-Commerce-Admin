package com.example.m_commerce_admin.features.products.domain.usecase.rest

import com.example.m_commerce_admin.core.shared.components.usecase.UseCase
import com.example.m_commerce_admin.features.products.domain.repository.RestProductRepository
import com.example.m_commerce_admin.features.products.domain.usecase.params.UploadExistProductImagesParams
import javax.inject.Inject

class UploadImagesToExistingProductUseCase @Inject constructor(
    private val repository: RestProductRepository
) : UseCase<UploadExistProductImagesParams, Result<Unit>> {

    override suspend fun invoke(params: UploadExistProductImagesParams): Result<Unit> {
        return repository.uploadImagesForExistingProduct(
            productId = params.productId,
            imageUris = params.imageUris,
            context = params.context
        )
    }
}

