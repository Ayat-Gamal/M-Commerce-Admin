package com.example.m_commerce_admin.features.coupons.domain.usecase

import com.example.m_commerce_admin.core.shared.components.usecase.UseCase
import com.example.m_commerce_admin.features.coupons.domain.entity.CouponItem
import com.example.m_commerce_admin.features.coupons.domain.repository.CouponRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllCouponsUseCase @Inject constructor(
    private val repository: CouponRepository
): UseCase<Unit, Flow<List<CouponItem>>>{
     override suspend fun invoke(params: Unit):Flow<List<CouponItem>> {
     return repository.getCoupons()
    }


} 