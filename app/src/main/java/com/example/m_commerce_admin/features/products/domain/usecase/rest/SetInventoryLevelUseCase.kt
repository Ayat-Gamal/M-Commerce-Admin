package com.example.m_commerce_admin.features.products.domain.usecase.rest

import com.example.m_commerce_admin.features.inventory.domain.repository.InventoryRepository
import com.example.m_commerce_admin.features.products.domain.repository.ProductRepository
import com.example.m_commerce_admin.features.products.domain.repository.RestProductRepository
import javax.inject.Inject

class SetInventoryLevelUseCase @Inject constructor(
    private val repository: RestProductRepository
) {
    suspend operator fun invoke(inventoryItemId: Long, locationId: Long, available: Int): Result<Unit> {
        return repository.setInventoryLevel(locationId, inventoryItemId, available)
    }
}
