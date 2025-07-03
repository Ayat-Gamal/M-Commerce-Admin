package com.example.m_commerce_admin.features.products.domain.usecase.params

data class GetProductsParams(
    val first: Int,
    val after: String? = null
)
