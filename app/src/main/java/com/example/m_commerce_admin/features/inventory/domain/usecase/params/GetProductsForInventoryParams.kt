package com.example.m_commerce_admin.features.inventory.domain.usecase.params

data class GetProductsForInventoryParams(
    val limit: Int = 250,
    val pageInfo: String? = null,
    val status: String? = null
)