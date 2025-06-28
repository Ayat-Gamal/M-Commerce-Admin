package com.example.m_commerce_admin.features.inventory.domain.usecase.params

data class AdjustInventoryLevelParam  (
    val inventoryItemId: Long,
    val  adjustment: Int
)