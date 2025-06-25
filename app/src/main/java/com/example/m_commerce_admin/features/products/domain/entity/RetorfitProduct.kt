package com.example.m_commerce_admin.features.products.domain.entity

import com.example.m_commerce_admin.features.products.data.retrofitRemote.ProductDto
import com.example.m_commerce_admin.features.products.data.retrofitRemote.VariantDto

data class RetrofitProduct(
    val id: Long,
    val title: String,
    val descriptionHtml: String,
    val productType: String,
    val vendor: String,
    val variants: List<RetrofitProductVariant>
)

data class RetrofitProductVariant(
    val id: Long,
    val sku: String,
    val price: String,
    val inventoryItemId: Long,
    val quantity: Int
)

fun ProductDto.toDomain() = RetrofitProduct(
    id = id,
    title = title,
    descriptionHtml = descriptionHtml.orEmpty(),
    productType = productType.orEmpty(),
    vendor = vendor.orEmpty(),
    variants = variants.map { it.toDomain() }
)

fun VariantDto.toDomain() = RetrofitProductVariant(
    id = id,
    sku = sku.orEmpty(),
    price = price,
    inventoryItemId = inventoryItemId,
    quantity = quantity
)
