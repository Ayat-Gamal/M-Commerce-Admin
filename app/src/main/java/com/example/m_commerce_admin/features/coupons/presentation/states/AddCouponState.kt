package com.example.m_commerce_admin.features.coupons.presentation.states

sealed class DeleteCouponState {
    object Idle : DeleteCouponState()
    object Loading : DeleteCouponState()
    object Success : DeleteCouponState()
    data class Error(val message: String) : DeleteCouponState()
}

sealed class CouponFormState {
    object Idle : CouponFormState()
    object Loading : CouponFormState()
    object Success : CouponFormState()
    data class Error(val message: String) : CouponFormState()
}
