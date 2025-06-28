package com.example.m_commerce_admin.features.coupons.presentation.viewModel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.m_commerce_admin.features.coupons.domain.entity.CouponInput
import com.example.m_commerce_admin.features.coupons.domain.entity.CouponItem
import com.example.m_commerce_admin.features.coupons.domain.entity.DiscountType
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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class CouponsViewModel @Inject constructor(
    private val getAllCouponsUseCase: GetAllCouponsUseCase,
    private val addCouponUseCase: AddCouponUseCase,
    private val updateCouponUseCase: UpdateCouponUseCase,
    private val deleteCouponUseCase: DeleteCouponUseCase
) : ViewModel() {
    
    // Original coupons data
    private val _allCoupons = MutableStateFlow<List<CouponItem>>(emptyList())
    
    // Filtered coupons for UI
    private val _coupons = MutableStateFlow<List<CouponItem>>(emptyList())
    val coupons: StateFlow<List<CouponItem>> = _coupons.asStateFlow()

    // Search and filter state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedFilter = MutableStateFlow(CouponFilter.ALL)
    val selectedFilter: StateFlow<CouponFilter> = _selectedFilter.asStateFlow()

    private val _couponFormState = MutableStateFlow<CouponFormState>(CouponFormState.Idle)
    val couponFormState: StateFlow<CouponFormState> = _couponFormState.asStateFlow()

    private val _deleteCouponState = MutableStateFlow<DeleteCouponState>(DeleteCouponState.Idle)
    val deleteCouponState: StateFlow<DeleteCouponState> = _deleteCouponState.asStateFlow()

    private var lastCouponInput: CouponInput? = null
    private var isLastOperationUpdate = false
    private var lastDeleteCode: String? = null

    fun fetchAllCoupons() {
        viewModelScope.launch {
            runCatching {
                getAllCouponsUseCase(Unit).collect { result ->
                    _allCoupons.emit(result)
                    applyFilters()
                    Log.d("TAG", "fetchAllCoupons: $result")
                }
            }.onFailure { exception ->
                Log.e("TAG", "Error fetching coupons", exception)

             }
        }
    }

    // Filtering functions
    @RequiresApi(Build.VERSION_CODES.O)
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        applyFilters()
    }

    fun updateFilter(filter: CouponFilter) {
        _selectedFilter.value = filter
        applyFilters()
    }

    fun clearFilters() {
        _searchQuery.value = ""
        _selectedFilter.value = CouponFilter.ALL
        applyFilters()
    }

    private fun applyFilters() {
        val query = _searchQuery.value.lowercase()
        val filter = _selectedFilter.value
        val allItems = _allCoupons.value

        val filtered = allItems.filter { coupon ->
            val matchesSearch = query.isEmpty() || 
                coupon.code.lowercase().contains(query) ||
                coupon.title?.lowercase()?.contains(query) == true ||
                coupon.summary?.lowercase()?.contains(query) == true
            
            val matchesFilter = when (filter) {
                CouponFilter.ALL -> true
                CouponFilter.ACTIVE -> isCouponActive(coupon)
                CouponFilter.EXPIRED -> isCouponExpired(coupon)
                CouponFilter.UNUSED -> (coupon.usedCount ?: 0) == 0
                CouponFilter.USED -> (coupon.usedCount ?: 0) > 0
                CouponFilter.PERCENTAGE -> coupon.value != null && coupon.amount == 0.0
                CouponFilter.FIXED_AMOUNT -> coupon.value == null && coupon.amount > 0
            }
            
            matchesSearch && matchesFilter
        }

        _coupons.value = filtered
    }

    private fun isCouponActive(coupon: CouponItem): Boolean {
        val now = LocalDateTime.now()
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        
        val startsAt = coupon.startsAt?.let { 
            try { LocalDateTime.parse(it, formatter) } catch (e: Exception) { null }
        }
        val endsAt = coupon.endsAt?.let { 
            try { LocalDateTime.parse(it, formatter) } catch (e: Exception) { null }
        }
        
        val hasStarted = startsAt == null || now.isAfter(startsAt)
        val hasNotEnded = endsAt == null || now.isBefore(endsAt)
        
        return hasStarted && hasNotEnded
    }

    private fun isCouponExpired(coupon: CouponItem): Boolean {
        val now = LocalDateTime.now()
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        
        val endsAt = coupon.endsAt?.let { 
            try { LocalDateTime.parse(it, formatter) } catch (e: Exception) { null }
        }
        
        return endsAt != null && now.isAfter(endsAt)
    }

    fun addCoupon(coupon: CouponInput) {
        viewModelScope.launch {
            try {
                _couponFormState.value = CouponFormState.Loading

                lastCouponInput = coupon
                isLastOperationUpdate = false

                val result = addCouponUseCase(coupon)

                if (result.isSuccess) {
                    fetchAllCoupons()
                    _couponFormState.value = CouponFormState.Success
                } else {
                    val errorMessage = result.exceptionOrNull()?.message ?: "Unknown error occurred"
                    _couponFormState.value = CouponFormState.Error(errorMessage)
                }
            } catch (e: Exception) {
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
                _couponFormState.value = CouponFormState.Loading

                lastCouponInput = coupon
                isLastOperationUpdate = true

                val result = updateCouponUseCase(coupon)

                if (result.isSuccess) {
                    fetchAllCoupons()
                    _couponFormState.value = CouponFormState.Success
                } else {
                    val errorMessage = result.exceptionOrNull()?.message ?: "Update failed"
                    _couponFormState.value = CouponFormState.Error(errorMessage)
                }
            } catch (e: Exception) {
                _couponFormState.value =
                    CouponFormState.Error(e.message ?: "An unexpected error occurred during update")
            }
        }
    }

    fun deleteCoupon(code: String) {
        viewModelScope.launch {
            try {
                _deleteCouponState.value = DeleteCouponState.Loading

                lastDeleteCode = code

                val result = deleteCouponUseCase(code)

                if (result.isSuccess) {
                    fetchAllCoupons()
                    _deleteCouponState.value = DeleteCouponState.Success
                } else {
                    val errorMessage = result.exceptionOrNull()?.message ?: "Delete failed"
                    _deleteCouponState.value = DeleteCouponState.Error(errorMessage)
                }
            } catch (e: Exception) {
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
