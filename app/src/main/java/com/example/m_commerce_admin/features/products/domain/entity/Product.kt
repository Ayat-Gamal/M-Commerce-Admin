package com.example.m_commerce_admin.features.products.domain.entity
data class Product(
    val id: String,
    val title: String,
    val status: String,
    val createdAt: String,
    val totalInventory: Int,
    val featuredImage:String?,
    val variants: String
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
    val price: String
)
