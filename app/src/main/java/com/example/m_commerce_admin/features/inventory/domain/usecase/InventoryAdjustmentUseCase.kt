package com.example.m_commerce_admin.features.inventory.domain.usecase

import com.example.m_commerce_admin.features.inventory.domain.entity.InventoryLevel
import com.example.m_commerce_admin.features.inventory.domain.repository.InventoryRepository
import javax.inject.Inject

class AdjustInventoryUseCase @Inject constructor(
    private val repository: InventoryRepository
) {
    suspend operator fun invoke(
        inventoryItemId: Long,
         adjustment: Int
    ): InventoryLevel {
        return repository.adjustInventoryLevel(inventoryItemId, adjustment)
    }
}
