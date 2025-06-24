package com.example.m_commerce_admin.features.products.data.model

import com.example.m_commerce_admin.type.StagedUploadTargetGenerateUploadResource


data class StagedUploadInput(
    val fileName: String,
    val mimeType: String,
    val resource: StagedUploadTargetGenerateUploadResource =
       StagedUploadTargetGenerateUploadResource.IMAGE
)
