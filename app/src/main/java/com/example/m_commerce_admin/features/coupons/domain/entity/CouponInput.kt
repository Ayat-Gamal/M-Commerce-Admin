package com.example.m_commerce_admin.features.coupons.domain.entity

data class CouponInput(
    val id: String? = null,

    val title: String?,
    val summary: String?,
    val code: String?,
    val startsAt: String?,
    val endsAt: String?,
    val usageLimit: Int?,
    val discountType: DiscountType,
    val discountValue: Double,
    val currencyCode: String? = "USD",
    val appliesOncePerCustomer: Boolean? = false
)

enum class DiscountType {
    PERCENTAGE,
    FIXED_AMOUNT
} 