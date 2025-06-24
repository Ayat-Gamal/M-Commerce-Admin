package com.example.m_commerce_admin.features.products.domain.entity

data class DomainProductInput(
    val title: String,
    val descriptionHtml: String,
    val productType: String,
    val vendor: String,
    val status: ProductStatus,
    val price: String,
    val category: String,
    val inStock: Boolean,
    val images: List<ProductImage> = emptyList()
)

data class ProductImage(
    val uri: String,
    val fileName: String,
    val mimeType: String
)

enum class ProductStatus {
    ACTIVE,
    DRAFT,
    ARCHIVED
}