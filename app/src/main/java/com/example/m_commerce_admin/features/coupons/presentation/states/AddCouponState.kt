package com.example.m_commerce_admin.features.coupons.presentation.states

sealed class AddCouponState {
    object Idle : AddCouponState()
    object Loading : AddCouponState()
    object Success : AddCouponState()
    data class Error(val message: String) : AddCouponState()
} 