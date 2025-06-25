package com.example.m_commerce_admin.features.inventory.data.dto

import com.google.gson.annotations.SerializedName

data class InventoryLevelDto(
    @SerializedName("inventory_item_id") val inventoryItemId: Long,
    @SerializedName("location_id") val locationId: Long,
    @SerializedName("available") val available: Int,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("admin_graphql_api_id") val graphqlApiId: String
)

data class InventoryLevelsResponse(
    @SerializedName("inventory_levels") val inventoryLevels: List<InventoryLevelDto>
)
