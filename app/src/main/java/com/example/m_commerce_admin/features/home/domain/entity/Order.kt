package com.example.m_commerce_admin.features.home.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val id: String,
    val name: String,
    val totalAmount: String,
    val currency: String,
    val createdAt: String,
    val status: String?,
    val customerName: String?,
    val customerEmail: String?,

)
