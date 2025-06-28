package com.example.m_commerce_admin.features.products.domain.entity.rest

data class RestProductImage(
    val id: Long,
    val src: String,
    val alt: String?,
    val width: Int?,
    val height: Int?,
    val position: Int?
)

data class RestProductImageInput(
    val src: String,
    val alt: String? = null,
    val position: Int? = null
)
