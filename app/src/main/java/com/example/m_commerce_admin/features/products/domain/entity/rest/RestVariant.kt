package com.example.m_commerce_admin.features.products.domain.entity.rest


data class RestProductVariant(
    val id: Long,
    val sku: String = "MY-SKU-121",
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

data class RestProductVariantInput(
    val option1: String? = null, // e.g., "Small"
    val option2: String? = null, // e.g., "Red"
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
    val requiresShipping: Boolean? = null,
    val inventoryQuantity: Int? = 0

)