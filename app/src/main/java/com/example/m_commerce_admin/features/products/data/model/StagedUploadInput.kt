package com.example.m_commerce_admin.features.products.data.model


data class StagedUploadInput(
    val fileName: String,
    val mimeType: String,
    val resource: com.example.m_commerce_admin.type.StagedUploadTargetGenerateUploadResource =
        com.example.m_commerce_admin.type.StagedUploadTargetGenerateUploadResource.IMAGE
)
