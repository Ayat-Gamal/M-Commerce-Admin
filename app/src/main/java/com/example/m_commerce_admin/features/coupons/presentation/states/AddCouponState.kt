package com.example.m_commerce_admin.features.coupons.presentation.states

sealed class AddCouponState {
    object Idle : AddCouponState()
    object Loading : AddCouponState()
    object Success : AddCouponState()
    data class Error(val message: String) : AddCouponState()
}

sealed class UpdateCouponState {
    object Idle : UpdateCouponState()
    object Loading : UpdateCouponState()
    object Success : UpdateCouponState()
    data class Error(val message: String) : UpdateCouponState()
}

sealed class DeleteCouponState {
    object Idle : DeleteCouponState()
    object Loading : DeleteCouponState()
    object Success : DeleteCouponState()
    data class Error(val message: String) : DeleteCouponState()
} 