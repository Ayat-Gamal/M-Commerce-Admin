package com.example.m_commerce_admin.features.products.data.remote

import com.example.m_commerce_admin.features.products.presentation.ProductState
import kotlinx.coroutines.flow.Flow

interface ProductRemoteDataSource {
    fun getProducts(first: Int, after: String?): Flow<ProductState>
}
