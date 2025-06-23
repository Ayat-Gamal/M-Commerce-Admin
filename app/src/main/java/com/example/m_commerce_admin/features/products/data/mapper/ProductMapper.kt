package com.example.m_commerce_admin.features.products.data.mapper

import com.example.m_commerce_admin.GetProductsQuery
import com.example.m_commerce_admin.features.products.domain.entity.Product

fun GetProductsQuery.Node.toDomain():Product {
    return Product(
        id = this.id,
        title = this.title,
        status = this.status.rawValue,
        createdAt = this.createdAt.toString(),
        totalInventory = this.totalInventory ?: 0,
        featuredImage = this.featuredImage?.url.toString() ?: "",
        variants = this.variants.edges.firstOrNull()?.node?.price.toString() ?: "0.00"
    )
}
