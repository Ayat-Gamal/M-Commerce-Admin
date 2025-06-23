package com.example.m_commerce_admin.features.products.data.mapper

import com.apollographql.apollo.api.Optional
import com.example.m_commerce_admin.AddProductMutation
import com.example.m_commerce_admin.features.products.domain.entity.DomainProductInput
import com.example.m_commerce_admin.features.products.domain.entity.ProductStatus
import com.example.m_commerce_admin.type.ProductInput
import com.example.m_commerce_admin.type.ProductStatus as GQLProductStatus

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

fun DomainProductInput.toGraphQL(): ProductInput {
    return ProductInput(
        title = Optional.presentIfNotNull(this.title),
        descriptionHtml = Optional.presentIfNotNull(this.descriptionHtml),
        productType = Optional.presentIfNotNull(this.productType),
        vendor = Optional.presentIfNotNull(this.vendor),
        status = Optional.presentIfNotNull(this.status.toGraphQLStatus())
    )
}

fun ProductStatus.toGraphQLStatus(): GQLProductStatus {
    return when (this) {
        ProductStatus.ACTIVE -> GQLProductStatus.ACTIVE
        ProductStatus.DRAFT -> GQLProductStatus.DRAFT
        ProductStatus.ARCHIVED -> GQLProductStatus.ARCHIVED
    }
}