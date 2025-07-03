package com.example.m_commerce_admin.features.inventory.presentation.state

import com.example.m_commerce_admin.features.inventory.domain.entity.InventoryLevel


sealed class InventoryLevelsState {
    object Loading : InventoryLevelsState()
    data class Success(val inventoryLevels: List<InventoryLevel>) : InventoryLevelsState()
    data class Error(val message: String) : InventoryLevelsState()

    object Empty : InventoryLevelsState()
}