package com.example.m_commerce_admin.features.coupons.domain.usecase

import com.example.m_commerce_admin.features.coupons.domain.entity.CouponInput
import com.example.m_commerce_admin.features.coupons.domain.repository.CouponRepository
import javax.inject.Inject

class UpdateCouponUseCase @Inject constructor(
    private val repository: CouponRepository
) {
    suspend operator fun invoke(couponInput: CouponInput): Result<Unit> {
        return repository.updateCoupon(couponInput)
    }
}
