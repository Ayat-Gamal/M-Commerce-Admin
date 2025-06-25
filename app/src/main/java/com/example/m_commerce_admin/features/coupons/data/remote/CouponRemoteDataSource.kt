package com.example.m_commerce_admin.features.coupons.data.remote

import com.example.m_commerce_admin.features.coupons.domain.entity.CouponInput
import com.example.m_commerce_admin.features.coupons.domain.entity.CouponItem
import kotlinx.coroutines.flow.Flow

interface CouponRemoteDataSource {
    fun getCoupons(): Flow<List<CouponItem>>
    suspend fun addCoupon(coupon: CouponInput): Result<Unit>
} 