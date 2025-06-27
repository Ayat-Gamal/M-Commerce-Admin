package com.example.m_commerce_admin.features.products.domain.entity.rest

data class RestProduct(
    val id: Long,
    val title: String,
    val descriptionHtml: String?,
    val productType: String?,
    val vendor: String?,
    val status: String?,
    val createdAt: String?,
    val updatedAt: String?,
    val publishedAt: String?,
    val templateSuffix: String?,
    val handle: String?,
    val tags: String?,
    val variants: List<RestProductVariant>,
    val images: List<RestProductImage>?,
    val options: List<RestProductOption>?
)

data class RestProductVariant(
    val id: Long,
    val sku: String?,
    val price: String,
    val inventoryItemId: Long,
    val quantity: Int,
    val title: String?,
    val weight: Double?,
    val weightUnit: String?,
    val barcode: String?,
    val inventoryManagement: String?,
    val inventoryPolicy: String?,
    val fulfillmentService: String?,
    val taxable: Boolean?,
    val requiresShipping: Boolean?
)

data class RestProductImage(
    val id: Long,
    val src: String,
    val alt: String?,
    val width: Int?,
    val height: Int?,
    val position: Int?
)

data class RestProductOption(
    val id: Long,
    val name: String,
    val position: Int,
    val values: List<String>?
)

// Input models for creating/updating products
data class RestProductInput(
    val title: String,
    val descriptionHtml: String? = null,
    val productType: String? = null,
    val vendor: String? = null,
    val status: String? = "draft",
    val tags: String? = null,
    val variants: List<RestProductVariantInput>? = null,
    val images: List<RestProductImageInput>? = null,
    val options: List<RestProductOptionInput>? = null
)

data class RestProductVariantInput(
    val price: String,
    val sku: String? = null,
    val title: String? = null,
    val weight: Double? = null,
    val weightUnit: String? = null,
    val barcode: String? = null,
    val inventoryManagement: String? = "shopify",
    val inventoryPolicy: String? = "deny",
    val fulfillmentService: String? = "manual",
    val taxable: Boolean? = true,
    val requiresShipping: Boolean? = true,
    val inventoryQuantity: Int? = 0
)

data class RestProductImageInput(
    val src: String,
    val alt: String? = null,
    val position: Int? = null
)

data class RestProductOptionInput(
    val name: String,
    val position: Int,
    val values: List<String>? = null
)

// Update models
data class RestProductUpdateInput(
    val title: String? = null,
    val descriptionHtml: String? = null,
    val productType: String? = null,
    val vendor: String? = null,
    val status: String? = null,
    val tags: String? = null,
    val variants: List<RestProductVariantUpdateInput>? = null,
    val images: List<RestProductImageInput>? = null,
    val options: List<RestProductOptionInput>? = null
)

data class RestProductVariantUpdateInput(
    val id: Long,
    val price: String? = null,
    val sku: String? = null,
    val title: String? = null,
    val weight: Double? = null,
    val weightUnit: String? = null,
    val barcode: String? = null,
    val inventoryManagement: String? = null,
    val inventoryPolicy: String? = null,
    val fulfillmentService: String? = null,
    val taxable: Boolean? = null,
    val requiresShipping: Boolean? = null
) 