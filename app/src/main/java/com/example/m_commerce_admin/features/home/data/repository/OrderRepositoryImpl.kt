package com.example.m_commerce_admin.features.home.data.repository

import com.example.m_commerce_admin.features.home.data.remote.OrderRemoteDataSource
import com.example.m_commerce_admin.features.home.domain.entity.Order
import com.example.m_commerce_admin.features.home.domain.repository.OrderRepository
import com.example.m_commerce_admin.features.home.presentation.HomeState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val remoteDataSource: OrderRemoteDataSource
) : OrderRepository {
    override fun getLastOrders(): Flow<HomeState<List<Order>>> {
        return remoteDataSource.getLastOrders()
    }
}