package com.example.m_commerce_admin.features.products.domain.repository

import android.content.Context
import android.net.Uri
import com.example.m_commerce_admin.features.products.domain.entity.DomainProductInput
import com.example.m_commerce_admin.features.products.presentation.states.GetProductState
import com.example.m_commerce_admin.type.ProductInput
import kotlinx.coroutines.flow.Flow

interface ProductRepository {

    fun getProducts(first: Int, after: String?): Flow<GetProductState>
    suspend fun addProduct(product: ProductInput): Result<Unit>
    suspend fun uploadImagesAndAddProduct(
        product: DomainProductInput,
        imageUris: List<Uri>,
        context: Context
    ): Result<Unit>

}