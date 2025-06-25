package com.example.m_commerce_admin.features.coupons.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.m_commerce_admin.features.coupons.domain.entity.CouponInput
import com.example.m_commerce_admin.features.coupons.domain.entity.CouponItem
import com.example.m_commerce_admin.features.coupons.domain.usecase.AddCouponUseCase
import com.example.m_commerce_admin.features.coupons.domain.usecase.DeleteCouponUseCase
import com.example.m_commerce_admin.features.coupons.domain.usecase.GetAllCouponsUseCase
import com.example.m_commerce_admin.features.coupons.domain.usecase.UpdateCouponUseCase
import com.example.m_commerce_admin.features.coupons.presentation.states.CouponFormState
import com.example.m_commerce_admin.features.coupons.presentation.states.DeleteCouponState
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val addCouponUseCase: AddCouponUseCase,
    private val updateCouponUseCase: UpdateCouponUseCase,
    private val deleteCouponUseCase: DeleteCouponUseCase
) : ViewModel() {
    private val _coupons = MutableStateFlow<List<CouponItem>>(emptyList())
    val coupons: StateFlow<List<CouponItem>> = _coupons.asStateFlow()

    private val _couponFormState = MutableStateFlow<CouponFormState>(CouponFormState.Idle)
    val couponFormState: StateFlow<CouponFormState> = _couponFormState.asStateFlow()


    private val _deleteCouponState = MutableStateFlow<DeleteCouponState>(DeleteCouponState.Idle)
    val deleteCouponState: StateFlow<DeleteCouponState> = _deleteCouponState.asStateFlow()

     private var lastCouponInput: CouponInput? = null
    private var isLastOperationUpdate = false
    private var lastDeleteCode: String? = null

    fun fetchAllCoupons() {
        getAllCouponsUseCase().onEach { couponsList ->
            _coupons.value = couponsList
        }.launchIn(viewModelScope)
    }

    fun addCoupon(coupon: CouponInput) {
        viewModelScope.launch {
            try {
                android.util.Log.d(
                    "TAG",
                    "CouponsViewModel: Starting add coupon with code: ${coupon.code}"
                )
                _couponFormState.value = CouponFormState.Loading

                lastCouponInput = coupon
                isLastOperationUpdate = false

                val result = addCouponUseCase(coupon)
                android.util.Log.d(
                    "TAG",
                    "CouponsViewModel: Add result success: ${result.isSuccess}"
                )

                if (result.isSuccess) {
                    android.util.Log.d(
                        "TAG",
                        "CouponsViewModel: Add successful, refreshing coupons"
                    )
                    fetchAllCoupons()
                    _couponFormState.value = CouponFormState.Success
                } else {
                    val errorMessage = result.exceptionOrNull()?.message ?: "Unknown error occurred"
                    android.util.Log.e(
                        "TAG",
                        "CouponsViewModel: Setting error state: $errorMessage"
                    )
                    _couponFormState.value = CouponFormState.Error(errorMessage)
                }
            } catch (e: Exception) {
                android.util.Log.e("TAG", "CouponsViewModel: Exception during add coupon", e)
                _couponFormState.value =
                    CouponFormState.Error(e.message ?: "An unexpected error occurred")
            }
        }
    }

    fun retryLastOperation() {
        lastCouponInput?.let { coupon ->
            if (isLastOperationUpdate) {
                updateCoupon(coupon)
            } else {
                addCoupon(coupon)
            }
        }
    }

    fun resetCouponFormState() {
        _couponFormState.value = CouponFormState.Idle
    }

    fun resetAddCouponState() {
        _couponFormState.value = CouponFormState.Idle
    }

    fun resetUpdateCouponState() {
        _couponFormState.value = CouponFormState.Idle
    }

    fun updateCoupon(coupon: CouponInput) {
        viewModelScope.launch {
            try {
                android.util.Log.d(
                    "TAG",
                    "CouponsViewModel: Starting update for coupon: ${coupon.id}"
                )
                _couponFormState.value = CouponFormState.Loading

                // Store for retry
                lastCouponInput = coupon
                isLastOperationUpdate = true

                val result = updateCouponUseCase(coupon)
                android.util.Log.d(
                    "TAG",
                    "CouponsViewModel: Update result success: ${result.isSuccess}"
                )

                if (result.isSuccess) {
                    android.util.Log.d(
                        "TAG",
                        "CouponsViewModel: Update successful, refreshing coupons"
                    )
                    fetchAllCoupons() // Refresh list
                    _couponFormState.value = CouponFormState.Success
                } else {
                    val errorMessage = result.exceptionOrNull()?.message ?: "Update failed"
                    android.util.Log.e(
                        "TAG",
                        "CouponsViewModel: Setting error state: $errorMessage"
                    )
                    _couponFormState.value = CouponFormState.Error(errorMessage)
                }
            } catch (e: Exception) {
                android.util.Log.e("TAG", "CouponsViewModel: Exception during update coupon", e)
                _couponFormState.value =
                    CouponFormState.Error(e.message ?: "An unexpected error occurred during update")
            }
        }
    }

    fun deleteCoupon(code: String) {
        viewModelScope.launch {
            try {
                android.util.Log.d(
                    "TAG",
                    "CouponsViewModel: Starting delete for coupon with code: $code"
                )
                _deleteCouponState.value = DeleteCouponState.Loading

                // Store for retry
                lastDeleteCode = code

                val result = deleteCouponUseCase(code)
                android.util.Log.d(
                    "TAG",
                    "CouponsViewModel: Delete result success: ${result.isSuccess}"
                )

                if (result.isSuccess) {
                    android.util.Log.d(
                        "TAG",
                        "CouponsViewModel: Delete successful, refreshing coupons"
                    )
                    fetchAllCoupons() // Refresh list
                    _deleteCouponState.value = DeleteCouponState.Success
                } else {
                    val errorMessage = result.exceptionOrNull()?.message ?: "Delete failed"
                    android.util.Log.e(
                        "TAG",
                        "CouponsViewModel: Setting error state: $errorMessage"
                    )
                    _deleteCouponState.value = DeleteCouponState.Error(errorMessage)
                }
            } catch (e: Exception) {
                android.util.Log.e("TAG", "CouponsViewModel: Exception during delete coupon", e)
                _deleteCouponState.value = DeleteCouponState.Error(
                    e.message ?: "An unexpected error occurred during delete"
                )
            }
        }
    }

    fun retryLastDeleteOperation() {
        lastDeleteCode?.let { code ->
            deleteCoupon(code)
        }
    }

    fun resetDeleteCouponState() {
        _deleteCouponState.value = DeleteCouponState.Idle
    }

}
