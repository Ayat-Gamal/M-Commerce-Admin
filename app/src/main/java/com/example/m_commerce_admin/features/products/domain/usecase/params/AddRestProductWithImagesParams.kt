package com.example.m_commerce_admin.features.products.domain.usecase.params

import android.content.Context
import android.net.Uri
import com.example.m_commerce_admin.features.products.domain.entity.rest.RestProductInput

data class AddRestProductWithImagesParams(
    val product: RestProductInput,
    val imageUris: List<Uri>,
    val context: Context
)