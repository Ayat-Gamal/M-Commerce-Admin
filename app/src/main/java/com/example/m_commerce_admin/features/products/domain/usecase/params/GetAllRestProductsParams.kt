package com.example.m_commerce_admin.features.products.domain.usecase.params

data class GetAllRestProductsParams(
    val limit: Int = 50,
    val pageInfo: String? = null,
    val status: String? = null
)