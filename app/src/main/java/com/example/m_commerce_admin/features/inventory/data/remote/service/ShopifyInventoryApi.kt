package com.example.m_commerce_admin.features.inventory.data.remote.service

import com.example.m_commerce_admin.BuildConfig
import com.example.m_commerce_admin.features.inventory.data.dto.ConnectInventoryRequest
import com.example.m_commerce_admin.features.inventory.data.dto.InventoryAdjustmentRequest
import com.example.m_commerce_admin.features.inventory.data.dto.InventoryAdjustmentResponse
import com.example.m_commerce_admin.features.inventory.data.dto.InventoryLevelsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ShopifyInventoryApi {

    @POST("inventory_levels/adjust.json")
    suspend fun adjustInventoryLevel(
        @Body request: InventoryAdjustmentRequest
    ): InventoryAdjustmentResponse

    @GET("inventory_levels.json")
    suspend fun getInventoryLevels(
        @Query("location_ids") locationIds: String = BuildConfig.locationID,
        @Query("limit") limit: Int = 250,
    ): InventoryLevelsResponse

    @POST("inventory_levels/connect.json")
    suspend fun connectInventoryLevel(@Body request: ConnectInventoryRequest): Response<Unit>


}

