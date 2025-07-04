package com.example.m_commerce_admin.features.products.domain.usecase

import com.example.m_commerce_admin.core.shared.components.usecase.UseCase
import com.example.m_commerce_admin.features.products.domain.entity.rest.RestProduct
import com.example.m_commerce_admin.features.products.domain.repository.RestProductRepository
import com.example.m_commerce_admin.features.products.domain.usecase.params.UploadProductImagesParams
import javax.inject.Inject

class UploadProductImagesUseCase @Inject constructor(
    private val repository: RestProductRepository
) : UseCase<UploadProductImagesParams, Result<RestProduct>> {

    override suspend fun invoke(params: UploadProductImagesParams): Result<RestProduct> {
        return repository.uploadImagesAndAddProduct(
            params.product,
            params.imageUris,
            params.context
        )
    }
}

