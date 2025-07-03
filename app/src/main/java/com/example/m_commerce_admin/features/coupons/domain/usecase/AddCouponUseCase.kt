package com.example.m_commerce_admin.features.coupons.domain.usecase

import com.example.m_commerce_admin.core.shared.components.usecase.UseCase
import com.example.m_commerce_admin.features.coupons.domain.entity.CouponInput
import com.example.m_commerce_admin.features.coupons.domain.repository.CouponRepository
import javax.inject.Inject

class AddCouponUseCase @Inject constructor(
    private val repository: CouponRepository
) : UseCase<CouponInput, Result<Unit>> {

    override suspend fun invoke(params: CouponInput): Result<Unit> {
        return repository.addCoupon(params)
    }
} 