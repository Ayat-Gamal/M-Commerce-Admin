package com.example.m_commerce_admin.features.inventory.data


data class InventoryItem(
    val id: String,
    val sku: String,
    val tracked: Boolean,
    val countryCodeOfOrigin: String?,
    val harmonizedSystemCode: String?,
    val createdAt: String,
    val duplicateSkuCount: Int,
    val inventoryHistoryUrl: String?
)

data class Location(
    val id: String,
    val name: String,
    val address1: String?,
    val address2: String?,
    val city: String?,
    val zip: String?,
    val country: String?,
    val phone: String?,
    val active: Boolean
)


data class InventoryLevel(
    val id: String,
    val available: Int,
    val incoming: Int,
    val committed: Int,
    val onHand: Int,
    val safetyStock: Int?,
    val reserved: Int?,
    val updatedAt: String,
    val item: InventoryItem,
    val location: Location
)
