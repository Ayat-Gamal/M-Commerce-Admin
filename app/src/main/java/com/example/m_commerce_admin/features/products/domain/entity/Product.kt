package com.example.m_commerce_admin.features.products.domain.entity

data class Product(
    val id: String,
    val title: String,
    val status: String,
    val createdAt: String,
    val totalInventory: Int,
    val featuredImage: String?,
    val price: String,
    val sku: String,
    val variantId: String,
    val variantTitle: String,
    val inventoryQuantity: Int,
    val images: List<String> = emptyList()
)

data class FeaturedImage(
    val url: String
)

data class Variants(
    val edges: List<VariantEdge>
)

data class VariantEdge(
    val node: Variant
)

data class Variant(
    val id: String,
    val title: String,
    val price: String,
    val sku: String,
    val inventoryQuantity: Int
)
