package com.example.m_commerce_admin.features.coupons.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.example.m_commerce_admin.features.coupons.domain.entity.CouponInput
import com.example.m_commerce_admin.features.coupons.domain.entity.CouponItem
import com.example.m_commerce_admin.features.coupons.domain.usecase.AddCouponUseCase
import com.example.m_commerce_admin.features.coupons.domain.usecase.GetAllCouponsUseCase
import com.example.m_commerce_admin.features.coupons.presentation.states.AddCouponState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CouponsViewModel @Inject constructor(
    private val getAllCouponsUseCase: GetAllCouponsUseCase,
    private val addCouponUseCase: AddCouponUseCase
) : ViewModel() {
    private val _coupons = MutableStateFlow<List<CouponItem>>(emptyList())
    val coupons: StateFlow<List<CouponItem>> = _coupons.asStateFlow()

    private val _addCouponState = MutableStateFlow<AddCouponState>(AddCouponState.Idle)
    val addCouponState: StateFlow<AddCouponState> = _addCouponState.asStateFlow()



    fun fetchAllCoupons() {
        getAllCouponsUseCase().onEach { couponsList ->
            _coupons.value = couponsList
        }.launchIn(viewModelScope)
    }

    fun addCoupon(coupon: CouponInput) {
        viewModelScope.launch {
            _addCouponState.value = AddCouponState.Loading
            val result = addCouponUseCase(coupon)
            _addCouponState.value = if (result.isSuccess) {
                fetchAllCoupons()
                AddCouponState.Success
            } else {
                AddCouponState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    fun resetAddCouponState() {
        _addCouponState.value = AddCouponState.Idle
    }
}
