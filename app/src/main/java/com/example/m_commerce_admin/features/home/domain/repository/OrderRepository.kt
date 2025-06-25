package com.example.m_commerce_admin.features.home.domain.repository

import com.example.m_commerce_admin.features.home.domain.entity.Order
import com.example.m_commerce_admin.features.home.presentation.state.HomeState
import kotlinx.coroutines.flow.Flow

interface OrderRepository {

      fun getLastOrders() : Flow<HomeState<List<Order>>>
}