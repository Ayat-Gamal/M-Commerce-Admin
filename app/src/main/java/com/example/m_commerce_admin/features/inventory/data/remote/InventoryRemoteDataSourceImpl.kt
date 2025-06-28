package com.example.m_commerce_admin.features.inventory.data.remote

import android.util.Log
import com.example.m_commerce_admin.features.inventory.data.dto.ConnectInventoryRequest
import com.example.m_commerce_admin.features.inventory.data.dto.InventoryAdjustmentRequest
import com.example.m_commerce_admin.features.inventory.data.dto.SetInventoryLevelRequest
import com.example.m_commerce_admin.features.inventory.data.remote.service.ShopifyInventoryApi
import com.example.m_commerce_admin.features.inventory.domain.entity.InventoryLevel
import com.example.m_commerce_admin.features.inventory.domain.entity.toDomain
import com.google.gson.Gson
import kotlinx.coroutines.delay
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
// After product creation
        val LOCATION_ID = 82774655225
        val conn = ConnectInventoryRequest(
            location_id = LOCATION_ID,
            inventory_item_id = inventoryItemId
        )

        // 2. Then adjust
        val connectResponse = api.connectInventoryLevel(conn)
        Log.d("DEBUG", "Connected inventory: ${connectResponse.code()} ${connectResponse.isSuccessful}")
        delay(2000)
        val adjustmentRequest = InventoryAdjustmentRequest(
            inventoryItemId,
            locationId = LOCATION_ID,
            availableAdjustment
        )


        Log.d("DEBUG", "Sending inventory adjustment request: $adjustmentRequest")

        val response = api.adjustInventoryLevel(
            InventoryAdjustmentRequest(
                inventoryItemId,
                locationId = LOCATION_ID,
                availableAdjustment
            )
        )
        Log.d("DEBUG", "adjustInventoryLevel: ${response.level}")
        Log.d("DEBUG", "Raw inventory response: ${response}")
        val level = response.level
        if (level == null) {
            throw IllegalStateException("Inventory adjustment failed: response.level is null")
        }
        Log.d("Inventory", "Raw adjustment response: ${Gson().toJson(response)}")

        return level.toDomain()

    }



}
