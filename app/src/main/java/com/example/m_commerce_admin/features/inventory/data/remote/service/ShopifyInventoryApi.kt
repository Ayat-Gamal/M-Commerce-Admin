package com.example.m_commerce_admin.features.inventory.data.remote.service

import com.example.m_commerce_admin.features.inventory.data.dto.InventoryLevelsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ShopifyInventoryApi {

    @GET("inventory_levels.json")
    suspend fun getInventoryLevels(
        @Query("location_ids") locationIds: String = "82774655225"
    ): InventoryLevelsResponse

}

