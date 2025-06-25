package com.example.m_commerce_admin.features.coupons.domain.entity

data class DomainCoupon(
    val title: String,
    val value: Double,
    val usageLimit: Int,
    val startsAt: String,
    val endsAt: String,
    val code: String
)
