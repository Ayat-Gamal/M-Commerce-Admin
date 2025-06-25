package com.example.m_commerce_admin.features.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.m_commerce_admin.features.inventory.domain.entity.InventoryLevel
import com.example.m_commerce_admin.features.inventory.domain.usecase.AdjustInventoryUseCase
import com.example.m_commerce_admin.features.inventory.domain.usecase.GetInventoryLevelsUseCase
import com.example.m_commerce_admin.features.inventory.presentation.state.InventoryLevelsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val getInventoryLevelsUseCase: GetInventoryLevelsUseCase,
    private val adjustInventoryUseCase: AdjustInventoryUseCase

) : ViewModel() {

    private val _uiState = MutableStateFlow<InventoryLevelsState>(InventoryLevelsState.Loading)
    val uiState: StateFlow<InventoryLevelsState> = _uiState.asStateFlow()
    private val _adjustState = MutableStateFlow<Result<InventoryLevel>?>(null)
    val adjustState: StateFlow<Result<InventoryLevel>?> = _adjustState

    private val _stockLevel = MutableStateFlow<List<InventoryLevel>?>(null)
    val stockLevel: StateFlow<List<InventoryLevel>?> = _stockLevel

    init {
        loadInventoryData()
        getLowStock()
    }

    fun getLowStock() {
        viewModelScope.launch {
            getInventoryLevelsUseCase()
                .collect { result ->
                    result
                        .onSuccess { levels ->
                            if (levels.isEmpty()) {
                                _stockLevel.emit(emptyList())
                            } else {
                                _stockLevel.emit(InventoryLevelsState.Success(levels).inventoryLevels.filter { it.available < 5 })
                            }
                        }
                        .onFailure { throwable ->
                            val errorMessage =
                                throwable.localizedMessage ?: "An unknown error occurred."
                            _stockLevel.emit(emptyList())
                        }
                }
        }
    }

    fun adjustInventoryLevel(inventoryItemId: Long, adjustment: Int) {
        viewModelScope.launch {
            try {
                val level = adjustInventoryUseCase(inventoryItemId, adjustment)
                _adjustState.value = Result.success(level)
                refreshInventoryData()

            } catch (e: Exception) {
                _adjustState.value = Result.failure(e)
            }
        }
    }

    fun loadInventoryData() {
        viewModelScope.launch {
            _uiState.value = InventoryLevelsState.Loading

            getInventoryLevelsUseCase()
                .collect { result ->
                    result
                        .onSuccess { levels ->
                            if (levels.isEmpty()) {
                                _uiState.value = InventoryLevelsState.Empty // Explicit empty state
                            } else {
                                _uiState.value = InventoryLevelsState.Success(levels)
                            }
                        }
                        .onFailure { throwable ->
                            val errorMessage =
                                throwable.localizedMessage ?: "An unknown error occurred."
                            _uiState.value = InventoryLevelsState.Error(errorMessage)
                        }
                }
        }
    }

    fun refreshInventoryData() {
        loadInventoryData()
        getLowStock()
    }

}
