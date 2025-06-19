package com.example.m_commerce_admin.features.inventory.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.m_commerce_admin.config.theme.*
import com.example.m_commerce_admin.core.helpers.formatCreatedAt
import com.example.m_commerce_admin.features.inventory.data.InventoryItem

@Composable
fun InventoryCard(
    inventoryItem: InventoryItem,
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = LightTeal),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .border(BorderStroke(1.dp, Teal), RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

             Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Item ID: ${inventoryItem.id}",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = DarkestGray
                )

                Row {
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Teal)
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = lightRed)
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // SKU
            Text(
                text = "SKU: ${inventoryItem.sku}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = DarkGray
            )

            // Tracked status
            Text(
                text = "Tracked: ${if (inventoryItem.tracked) "Yes" else "No"}",
                fontSize = 13.sp,
                color = if (inventoryItem.tracked) LightGreen else lightRed
            )

            // Duplicate SKU count
            Text(
                text = "Duplicate SKUs: ${inventoryItem.duplicateSkuCount}",
                fontSize = 13.sp,
                color = DarkGray
            )

            // Country of origin (optional)
            inventoryItem.countryCodeOfOrigin?.let {
                Text(
                    text = "Country of Origin: $it",
                    fontSize = 13.sp,
                    color = DarkGray
                )
            }

            // Harmonized System Code (optional)
            inventoryItem.harmonizedSystemCode?.let {
                Text(
                    text = "HS Code: $it",
                    fontSize = 13.sp,
                    color = DarkGray
                )
            }

            // Created at badge
            Text(
                text = "Created at: ${formatCreatedAt(20220523)}",
                fontSize = 13.sp,
                color = White,
                modifier = Modifier
                    .padding(top = 6.dp)
                    .background(color = LightGreen, shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }
    }
}
