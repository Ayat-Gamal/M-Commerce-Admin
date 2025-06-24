package com.example.m_commerce_admin.features.products.data.model


data class StagedUploadTarget(
    val url: String,
    val resourceUrl: String,
    val parameters: Map<String, String>
)
