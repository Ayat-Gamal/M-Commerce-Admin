package com.example.m_commerce_admin.features.products.domain.usecase.params

import com.example.m_commerce_admin.features.products.domain.entity.rest.RestProductUpdateInput


data class UpdateRestProductParams(
    val productId: Long,
    val product: RestProductUpdateInput
)