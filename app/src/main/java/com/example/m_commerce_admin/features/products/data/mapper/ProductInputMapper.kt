package com.example.m_commerce_admin.features.products.data.mapper

import com.example.m_commerce_admin.AddProductMutation
import com.example.m_commerce_admin.features.products.domain.entity.Product

/*
fun AddProductMutation.Product.toDomain(): Product {
    return Product(
        id = id,
        title = title,
        status = status.rawValue,
        createdAt = createdAt.toString(),
        totalInventory = totalInventory ?: 0,
        featuredImage = featuredImage?.url.toString()
    )
}

fun GQLProductInput.toGraphQL(): GQLProductInput {
    return GQLProductInput.builder()
        .title(this.title)
        .descriptionHtml(this.descriptionHtml)
        .productType(this.productType)
        .vendor(this.vendor)
        .status(this.status.toGraphQLStatus())
        .build()
}
fun ProductStatus.toGraphQLStatus(): GQLProductStatus {
    return when (this) {
        ProductStatus.ACTIVE -> GQLProductStatus.ACTIVE
        ProductStatus.DRAFT -> GQLProductStatus.DRAFT
        ProductStatus.ARCHIVED -> GQLProductStatus.ARCHIVED
    }
}

*/