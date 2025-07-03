package com.example.m_commerce_admin.features.coupons.domain.usecase

import com.example.m_commerce_admin.core.shared.components.usecase.UseCase
import com.example.m_commerce_admin.features.coupons.domain.repository.CouponRepository
import javax.inject.Inject

class DeleteCouponUseCase @Inject constructor(
    private val repository: CouponRepository
) : UseCase<String, Result<Unit>> {

    override suspend fun invoke(params: String): Result<Unit> {
        return repository.deleteCoupon(params)
    }
} 