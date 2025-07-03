package com.example.m_commerce_admin.features.products.domain.usecase.params

import android.content.Context
import android.net.Uri

data class UploadExistProductImagesParams(
    val productId: Long,
    val imageUris: List<Uri>,
    val context: Context
)