package com.example.m_commerce_admin.features.inventory.presentation.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.m_commerce_admin.config.theme.DarkestGray
import com.example.m_commerce_admin.config.theme.LightGreen
import com.example.m_commerce_admin.config.theme.Teal
import com.example.m_commerce_admin.config.theme.White
import com.example.m_commerce_admin.core.helpers.formatIsoDate
import com.example.m_commerce_admin.features.inventory.domain.entity.InventoryLevel


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun InventoryCard(
    data: InventoryLevel,
    onEdit: (Long) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(color = White),
        colors = CardDefaults.cardColors(containerColor = White),

        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Item ID: ${data.inventoryItemId}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = DarkestGray
                )
                Row {
                    IconButton(onClick = { onEdit(data.inventoryItemId) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Teal)
                    }
                }
            }
            Text(
                "Available:${data.available}",
                modifier = Modifier
                    .padding(top = 6.dp)
                    .background(color = LightGreen, shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "updated At: ${formatIsoDate(data.updatedAt)}",
            )

        }
    }
}

