package com.example.m_commerce_admin.features.inventory.data.dto

import com.google.gson.annotations.SerializedName

data class InventoryItemResponse(
    @SerializedName("inventory_item") val inventoryItem: InventoryItemDto
)

data class InventoryItemDto(
    @SerializedName("id") val id: Long,
    @SerializedName("sku") val sku: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?,
    @SerializedName("requires_shipping") val requiresShipping: Boolean,
    @SerializedName("cost") val cost: String?,
    @SerializedName("country_code_of_origin") val countryCodeOfOrigin: String?,
    @SerializedName("province_code_of_origin") val provinceCodeOfOrigin: String?,
    @SerializedName("harmonized_system_code") val harmonizedSystemCode: String?,
    @SerializedName("tracked") val tracked: Boolean,
    @SerializedName("country_harmonized_system_codes") val countryHarmonizedSystemCodes: List<String>,
    @SerializedName("admin_graphql_api_id") val graphqlApiId: String
)