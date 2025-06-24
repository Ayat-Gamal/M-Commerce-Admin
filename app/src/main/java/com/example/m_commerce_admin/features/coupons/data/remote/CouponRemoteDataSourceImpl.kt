package com.example.m_commerce_admin.features.coupons.data.remote

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.exception.ApolloException
import com.example.m_commerce_admin.GetCouponsQuery
import com.example.m_commerce_admin.features.coupons.data.toCouponItems
import com.example.m_commerce_admin.features.coupons.domain.entity.CouponItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CouponRemoteDataSourceImpl @Inject constructor(
    private val apolloClient: ApolloClient
) : CouponRemoteDataSource {
    override fun getCoupons(): Flow<List<CouponItem>> = flow {
        try {
            val response = apolloClient.query(GetCouponsQuery(first = 20)).execute()
            if (response.hasErrors()) {
                emit(emptyList())
                return@flow
            }
            val data = response.data
            if (data != null) {
                emit(data.toCouponItems())
            } else {
                emit(emptyList())
            }
        } catch (e: ApolloException) {
            emit(emptyList())
        }
    }
} 