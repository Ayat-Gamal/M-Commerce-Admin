package com.example.m_commerce_admin.features.products.domain.usecase

import android.content.Context
import android.net.Uri
import com.example.m_commerce_admin.core.shared.components.usecase.UseCase
import com.example.m_commerce_admin.features.products.domain.entity.RestProduct
import com.example.m_commerce_admin.features.products.domain.entity.RestProductInput
import com.example.m_commerce_admin.features.products.domain.repository.RestProductRepository
import javax.inject.Inject

class UploadProductImagesUseCase @Inject constructor(
    private val repository: RestProductRepository
) : UseCase<UploadProductImagesParams, Result<RestProduct>> {

    override suspend fun invoke(params: UploadProductImagesParams): Result<RestProduct> {
        return repository.uploadImagesAndAddProduct(params.product, params.imageUris, params.context)
    }
}

data class UploadProductImagesParams(
    val product: RestProductInput,
    val imageUris: List<Uri>,
    val context: Context
) 