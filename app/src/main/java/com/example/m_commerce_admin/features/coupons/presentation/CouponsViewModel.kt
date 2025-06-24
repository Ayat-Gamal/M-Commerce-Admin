package com.example.m_commerce_admin.features.coupons.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.example.m_commerce_admin.features.coupons.domain.entity.CouponItem
import com.example.m_commerce_admin.features.coupons.domain.usecase.GetAllCouponsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class CouponsViewModel @Inject constructor(
    private val getAllCouponsUseCase: GetAllCouponsUseCase
) : ViewModel() {
    private val _coupons = MutableStateFlow<List<CouponItem>>(emptyList())
    val coupons: StateFlow<List<CouponItem>> = _coupons.asStateFlow()

    init {
        getCoupons()
    }

    private fun getCoupons() {
        getAllCouponsUseCase().onEach { couponsList ->
            _coupons.value = couponsList
        }.launchIn(viewModelScope)
    }
} 