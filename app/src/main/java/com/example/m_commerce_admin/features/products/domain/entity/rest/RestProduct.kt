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
