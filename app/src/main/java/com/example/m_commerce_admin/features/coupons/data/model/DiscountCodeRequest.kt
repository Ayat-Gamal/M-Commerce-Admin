package com.example.m_commerce_admin.features.coupons.data.model

data class DiscountCodeRequest(
    val discount_code: DiscountCode
)

data class DiscountCode(
    val code: String
)
