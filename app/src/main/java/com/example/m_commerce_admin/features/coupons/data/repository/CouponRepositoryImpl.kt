package com.example.m_commerce_admin.features.coupons.data.repository

import com.example.m_commerce_admin.features.coupons.data.remote.CouponRemoteDataSource
import com.example.m_commerce_admin.features.coupons.domain.entity.CouponItem
import com.example.m_commerce_admin.features.coupons.domain.repository.CouponRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class CouponRepositoryImpl @Inject constructor(
    private val remoteDataSource: CouponRemoteDataSource
) : CouponRepository {
    override fun getCoupons(): Flow<List<CouponItem>> = remoteDataSource.getCoupons()
} 