package com.example.m_commerce_admin.features.inventory

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.m_commerce_admin.features.inventory.component.InventoryCard
import com.example.m_commerce_admin.features.inventory.data.InventoryItem

@Composable
fun InventoryScreenUI() {
    val dummyInventoryList = listOf(
        InventoryItem(
            id = "INV-001",
            sku = "SHIRT-RED-M",
            tracked = true,
            countryCodeOfOrigin = "US",
            harmonizedSystemCode = "620520",
            createdAt = "2025-05-01T10:00:00Z",
            duplicateSkuCount = 2,
            inventoryHistoryUrl = " "
        ),
        InventoryItem(
            id = "INV-002",
            sku = "PANTS-BLUE-L",
            tracked = false,
            countryCodeOfOrigin = "CN",
            harmonizedSystemCode = "620342",
            createdAt = "2025-05-10T09:00:00Z",
            duplicateSkuCount = 0,
            inventoryHistoryUrl = "https://shopify.com/history/INV-002"
        ),
        InventoryItem(
            id = "INV-003",
            sku = "JACKET-BLK-XL",
            tracked = true,
            countryCodeOfOrigin = "EG",
            harmonizedSystemCode = null,
            createdAt = "2025-06-01T12:00:00Z",
            duplicateSkuCount = 5,
            inventoryHistoryUrl = null
        )
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            items(dummyInventoryList.size) { index ->
                InventoryCard(
                    inventoryItem = dummyInventoryList[index],
                    onEditClick = { /* TODO: Handle edit */ },
                    onDeleteClick = { /* TODO: Handle delete */ }
                )
            }
        }
    }
}
