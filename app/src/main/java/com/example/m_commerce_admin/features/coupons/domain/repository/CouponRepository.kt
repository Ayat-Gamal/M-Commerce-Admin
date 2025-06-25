package com.example.m_commerce_admin.features.coupons.domain.repository

import com.example.m_commerce_admin.features.coupons.domain.entity.CouponInput
import com.example.m_commerce_admin.features.coupons.domain.entity.CouponItem
import kotlinx.coroutines.flow.Flow

interface CouponRepository {
    fun getCoupons(): Flow<List<CouponItem>>
    suspend fun addCoupon(coupon: CouponInput): Result<Unit>
    suspend fun updateCoupon(couponInput: CouponInput): Result<Unit>
    suspend fun deleteCoupon(couponCode: String): Result<Unit>
} 