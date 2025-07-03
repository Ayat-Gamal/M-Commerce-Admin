package com.example.m_commerce_admin.features.products.presentation.states

import com.example.m_commerce_admin.features.products.domain.entity.Product


sealed class GetProductState {
    object Loading : GetProductState()
    data class Success(
        val data: List<Product>,
        val hasNext: Boolean,
        val endCursor: String?
    ) : GetProductState()
    data class Error(val message: String) : GetProductState()
}
