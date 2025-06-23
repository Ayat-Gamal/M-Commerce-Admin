package com.example.m_commerce_admin.features.products.domain.entity
data class ProductInput(
    val title: String,
    val descriptionHtml: String,
    val productType: String,
    val vendor: String,
    val status: ProductStatus,
    val imageUrl: String?, // optional
    val price: Double,
    val totalInventory: Int,
    val variantName: String // for now one variant
)
enum class ProductStatus {
    ACTIVE, DRAFT, ARCHIVED
}