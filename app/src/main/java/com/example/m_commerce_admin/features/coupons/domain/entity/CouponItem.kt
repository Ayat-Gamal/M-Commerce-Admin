package com.example.m_commerce_admin.features.coupons.domain.entity

data class CouponItem(
    val code: String,
    val value: Double?,
    val usedCount: Int?,
    val startsAt: String?,
    val endsAt: String?,
    val title: String?,
    val summary: String?,
    val usageLimit: Int?,
    val createdAt: String?,
    val updatedAt: String?,
    val amount: Double,
    val currencyCode: String?
)