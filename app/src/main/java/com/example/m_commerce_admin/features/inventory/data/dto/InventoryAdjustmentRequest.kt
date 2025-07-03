package com.example.m_commerce_admin.features.inventory.data.dto

import com.google.gson.annotations.SerializedName

data class InventoryAdjustmentRequest(
    @SerializedName("inventory_item_id") val inventoryItemId: Long,
    @SerializedName("location_id") val locationId: Long,
    @SerializedName("available_adjustment") val availableAdjustment: Int
)

data class InventoryAdjustmentResponse(
    @SerializedName("inventory_level") val level: InventoryLevelDto
)

