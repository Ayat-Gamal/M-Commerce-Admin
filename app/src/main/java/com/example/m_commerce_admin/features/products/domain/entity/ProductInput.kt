package com.example.m_commerce_admin.features.products.domain.entity

data class ProductInput(
    val title: String,
    val descriptionHtml: String,
    val productType: String,
    val vendor: String,
    val status: ProductStatus
)


enum class ProductStatus {
    ACTIVE,
    DRAFT,
    ARCHIVED
}