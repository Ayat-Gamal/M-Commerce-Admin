package com.example.m_commerce_admin.features.products.domain.usecase.rest

import com.example.m_commerce_admin.core.shared.components.usecase.UseCase
import com.example.m_commerce_admin.features.products.domain.entity.rest.RestProduct
import com.example.m_commerce_admin.features.products.domain.repository.RestProductRepository
import com.example.m_commerce_admin.features.products.domain.usecase.params.AddRestProductWithImagesParams
import javax.inject.Inject

class AddRestProductWithImagesUseCase @Inject constructor(
    private val repository: RestProductRepository
) : UseCase<AddRestProductWithImagesParams, Result<RestProduct>> {

    override suspend fun invoke(params: AddRestProductWithImagesParams): Result<RestProduct> {
        return repository.uploadImagesAndAddProduct(
            product = params.product,
            imageUris = params.imageUris,
            context = params.context
        )
    }
}

