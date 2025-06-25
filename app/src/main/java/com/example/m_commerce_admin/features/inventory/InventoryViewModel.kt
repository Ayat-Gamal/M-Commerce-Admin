package com.example.m_commerce_admin.features.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.m_commerce_admin.features.inventory.domain.usecase.GetInventoryLevelsUseCase
import com.example.m_commerce_admin.features.inventory.presentation.state.InventoryLevelsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val getInventoryLevelsUseCase: GetInventoryLevelsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<InventoryLevelsState>(InventoryLevelsState.Loading)
    val uiState: StateFlow<InventoryLevelsState> = _uiState.asStateFlow()

    init {
        loadInventoryData()
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
    }

}
