package com.example.m_commerce_admin.features.inventory.domain.usecase

import com.example.m_commerce_admin.core.shared.components.usecase.UseCase
import com.example.m_commerce_admin.features.inventory.domain.entity.InventoryLevel
import com.example.m_commerce_admin.features.inventory.domain.repository.InventoryRepository
import com.example.m_commerce_admin.features.inventory.domain.usecase.params.AdjustInventoryLevelParam
import javax.inject.Inject

class AdjustInventoryUseCase @Inject constructor(
    private val repository: InventoryRepository
) : UseCase<AdjustInventoryLevelParam, InventoryLevel> {
    override suspend operator fun invoke(params: AdjustInventoryLevelParam): InventoryLevel {
        return repository.adjustInventoryLevel(params.inventoryItemId, params.adjustment)
    }
}

