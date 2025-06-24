package com.example.m_commerce_admin.features.coupons.domain.usecase

import com.example.m_commerce_admin.features.coupons.domain.entity.CouponItem
import com.example.m_commerce_admin.features.coupons.domain.repository.CouponRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllCouponsUseCase @Inject constructor(
    private val repository: CouponRepository
) {
    operator fun invoke(): Flow<List<CouponItem>> = repository.getCoupons()
} 