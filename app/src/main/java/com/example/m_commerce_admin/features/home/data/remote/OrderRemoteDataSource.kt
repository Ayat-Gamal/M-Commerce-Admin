package com.example.m_commerce_admin.features.home.data.remote

import com.example.m_commerce_admin.features.home.domain.entity.Order
import com.example.m_commerce_admin.features.home.presentation.HomeState
import kotlinx.coroutines.flow.Flow

interface OrderRemoteDataSource {

    fun getLastOrders(): Flow<HomeState<List<Order>>>

}