package com.example.m_commerce_admin.features.inventory.domain.entity

import com.example.m_commerce_admin.features.inventory.data.dto.InventoryLevelDto

data class InventoryLevel(
    val inventoryItemId: Long,
    val locationId: Long,
    val available: Int,
    val updatedAt: String,
    val graphqlApiId: String,
    val productTitle: String? = null,
    val productImage: String? = null,
    val productPrice: String? = null,
    val productSku: String? = null
)

fun InventoryLevelDto.toDomain(): InventoryLevel {
    return InventoryLevel(
        inventoryItemId = inventoryItemId,
        locationId = locationId,
        available = available,
        updatedAt = updatedAt,
        graphqlApiId = graphqlApiId
    )
}

