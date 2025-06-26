package com.example.m_commerce_admin.features.inventory.data.remote

import com.example.m_commerce_admin.features.inventory.data.dto.InventoryAdjustmentRequest
import com.example.m_commerce_admin.features.inventory.data.remote.service.ShopifyInventoryApi
import com.example.m_commerce_admin.features.inventory.domain.entity.InventoryLevel
import com.example.m_commerce_admin.features.inventory.domain.entity.toDomain
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class InventoryRemoteDataSourceImpl @Inject constructor(
    private val api: ShopifyInventoryApi
) : InventoryRemoteDataSource {

    override suspend fun getInventoryLevels(): Result<List<InventoryLevel>> {
        return runCatching {
            val response = api.getInventoryLevels()
            response.inventoryLevels.map { it.toDomain() }
        }.onFailure { e ->
            when (e) {
                is HttpException -> println("HTTP Error: ${e.code()} - ${e.message}")
                is IOException -> println("Network Error: ${e.message}")
                else -> println("Unknown Error: ${e.message}")
            }
        }
    }

    override suspend fun getInventoryWithProductDetails(): Result<List<InventoryLevel>> {
        return runCatching {
            val response = api.getInventoryLevels()
            response.inventoryLevels.map { it.toDomain() }
        }.onFailure { e ->
            when (e) {
                is HttpException -> println("HTTP Error: ${e.code()} - ${e.message}")
                is IOException -> println("Network Error: ${e.message}")
                else -> println("Unknown Error: ${e.message}")
            }
        }
    }

    override suspend fun adjustInventoryLevel(
        inventoryItemId: Long,
        locationId: Long,
        availableAdjustment: Int
    ): InventoryLevel {
        val response = api.adjustInventoryLevel(
            InventoryAdjustmentRequest(inventoryItemId, locationId = 82774655225, availableAdjustment)
        )
        return response.level.toDomain()
    }
}
