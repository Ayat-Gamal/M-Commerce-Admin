package com.example.m_commerce_admin.features.coupons.domain.repository

import com.example.m_commerce_admin.features.coupons.domain.entity.CouponItem
import kotlinx.coroutines.flow.Flow

interface CouponRepository {
    fun getCoupons(): Flow<List<CouponItem>>
} 