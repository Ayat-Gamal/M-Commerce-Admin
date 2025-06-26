package com.example.m_commerce_admin.features.products.data.mapper

import com.apollographql.apollo.api.Optional
import com.example.m_commerce_admin.features.products.domain.entity.DomainProductInput
import com.example.m_commerce_admin.features.products.domain.entity.ProductStatus
import com.example.m_commerce_admin.type.InventoryLevelInput
import com.example.m_commerce_admin.type.ProductCategoryInput
import com.example.m_commerce_admin.type.ProductInput
import com.example.m_commerce_admin.type.ProductVariantInput
import com.example.m_commerce_admin.type.ProductStatus as GQLProductStatus


fun DomainProductInput.toGraphQL(): ProductInput {
    val varList = listOf(
        ProductVariantInput(price = Optional.present(333), sku = Optional.present("BAG TEST"), inventoryQuantities = Optional.present(
            listOf(InventoryLevelInput(availableQuantity = 5,"82774655225")))),
        )


    return ProductInput(
        title = Optional.presentIfNotNull(this.title),
        descriptionHtml = Optional.presentIfNotNull(this.descriptionHtml),
        productType = Optional.presentIfNotNull(this.productType),
        vendor = Optional.presentIfNotNull(this.vendor),
        status = Optional.presentIfNotNull(this.status.toGraphQLStatus()),
        published = Optional.present(true),
       // productCategory = Optional.present(ProductCategoryInput("SHOES")),
     //   variants = Optional.present(varList)
    )
}


fun ProductStatus.toGraphQLStatus(): GQLProductStatus {
    return when (this) {
        ProductStatus.ACTIVE -> GQLProductStatus.ACTIVE
        ProductStatus.DRAFT -> GQLProductStatus.DRAFT
        ProductStatus.ARCHIVED -> GQLProductStatus.ARCHIVED
    }
}