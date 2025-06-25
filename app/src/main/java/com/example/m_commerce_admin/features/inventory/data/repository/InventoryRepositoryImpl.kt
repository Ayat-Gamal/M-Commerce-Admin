package com.example.m_commerce_admin.features.inventory.data.repository

import com.example.m_commerce_admin.features.inventory.data.remote.InventoryRemoteDataSource
import com.example.m_commerce_admin.features.inventory.domain.entity.InventoryLevel
import com.example.m_commerce_admin.features.inventory.domain.repository.InventoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject


class InventoryRepositoryImpl @Inject constructor(
    private val remote: InventoryRemoteDataSource
) : InventoryRepository {

    override suspend fun getInventoryLevels(): Flow<Result<List<InventoryLevel>>> = flow {
        emit(remote.getInventoryLevels())
    }.flowOn(Dispatchers.IO)

    override suspend fun adjustInventoryLevel(
        inventoryItemId: Long,
        availableAdjustment: Int
    ): InventoryLevel {
        return remote.adjustInventoryLevel(inventoryItemId, locationId = 82774655225, availableAdjustment)
    }

}


