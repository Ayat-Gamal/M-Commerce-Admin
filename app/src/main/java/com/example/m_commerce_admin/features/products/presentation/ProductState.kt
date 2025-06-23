package com.example.m_commerce_admin.features.products.presentation

import com.example.m_commerce_admin.features.products.domain.entity.Product


sealed class ProductState {
    object Loading : ProductState()
    data class Success(
        val data: List<Product>,
        val hasNext: Boolean,
        val endCursor: String?
    ) : ProductState()
    data class Error(val message: String) : ProductState()
}
