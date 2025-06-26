package com.example.m_commerce_admin.features.inventory.data.remote

import com.example.m_commerce_admin.features.inventory.domain.entity.InventoryLevel

interface InventoryRemoteDataSource {
    suspend fun getInventoryLevels(): Result<List<InventoryLevel>>
    
    suspend fun getInventoryWithProductDetails(): Result<List<InventoryLevel>>

    suspend fun adjustInventoryLevel(
        inventoryItemId: Long,
        locationId: Long,
        availableAdjustment: Int
    ): InventoryLevel
}