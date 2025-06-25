package com.example.m_commerce_admin.features.home.domain.usecase

import com.example.m_commerce_admin.core.shared.components.usecase.UseCase
import com.example.m_commerce_admin.features.home.domain.entity.Order
import com.example.m_commerce_admin.features.home.domain.repository.OrderRepository
import com.example.m_commerce_admin.features.home.presentation.state.HomeState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLastOrdersUseCase @Inject constructor(private  val repo : OrderRepository) : UseCase<Unit, Flow<HomeState<List<Order>>>> {
    override suspend fun invoke(params: Unit): Flow<HomeState<List<Order>>> {
        return repo.getLastOrders()
    }
}