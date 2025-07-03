package com.example.m_commerce_admin.features.coupons.presentation.viewModel

enum class CouponFilter(val displayName: String) {
    ALL("All Coupons"),
    ACTIVE("Active"),
    EXPIRED("Expired"),
    UNUSED("Unused"),
    USED("Used"),
    PERCENTAGE("Percentage Discount"),
    FIXED_AMOUNT("Fixed Amount")
} 