package com.example.m_commerce_admin.features.inventory.domain.repository

import com.example.m_commerce_admin.features.inventory.domain.entity.InventoryLevel
import kotlinx.coroutines.flow.Flow

interface InventoryRepository {

    // fun getInventoryItems(ids: List<Long>): Flow<List<InventoryItem>>
    suspend fun getInventoryLevels(): Flow<Result<List<InventoryLevel>>>
} 