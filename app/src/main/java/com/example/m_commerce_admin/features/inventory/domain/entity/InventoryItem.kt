package com.example.m_commerce_admin.features.inventory.domain.entity

import com.example.m_commerce_admin.features.inventory.data.dto.InventoryItemDto


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

data class InventoryItem(
    val id: Long,
    val sku: String,
    val cost: String,
    val tracked: Boolean,
    val requiresShipping: Boolean,
    val graphqlApiId: String
)


fun InventoryItemDto.toDomain(): InventoryItem {
    return InventoryItem(
        id = id,
        sku = sku.orEmpty(),
        cost = cost.orEmpty(),
        tracked = tracked,
        requiresShipping = requiresShipping,
        graphqlApiId = graphqlApiId
    )
}


