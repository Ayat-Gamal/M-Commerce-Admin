package com.example.m_commerce_admin.features.home.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.m_commerce_admin.features.home.domain.entity.Order
import com.example.m_commerce_admin.features.home.domain.usecase.GetLastOrdersUseCase
import com.example.m_commerce_admin.features.home.presentation.state.HomeState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getLastOrderUseCase: GetLastOrdersUseCase

) : ViewModel() {
    private val _orderState = MutableStateFlow<HomeState<List<Order>>>(HomeState.Loading)
    val orderState: StateFlow<HomeState<List<Order>>> = _orderState

    init {
        fetchOrders()

    }

    fun fetchOrders() {
        viewModelScope.launch {
            getLastOrderUseCase(Unit).collect { result ->
                _orderState.value = result
            }
        }
    }

}