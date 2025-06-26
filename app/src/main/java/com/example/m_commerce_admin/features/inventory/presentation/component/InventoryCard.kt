package com.example.m_commerce_admin.features.inventory.presentation.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
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
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Product Image
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(LightGreen),
                contentAlignment = Alignment.Center
            ) {
                if (data.productImage != null) {
                    AsyncImage(
                        model = data.productImage,
                        contentDescription = "Product Image",
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "No Image",
                        tint = Teal,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Product Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Product Title
                Text(
                    text = data.productTitle ?: "Item ID: ${data.inventoryItemId}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = DarkestGray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Price and SKU
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (data.productPrice != null) {
                        Text(
                            text = "$${data.productPrice}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Teal
                        )
                    }
                    
                    if (data.productSku != null) {
                        Text(
                            text = "SKU: ${data.productSku}",
                            fontSize = 12.sp,
                            color = DarkestGray.copy(alpha = 0.7f)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Inventory Information
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Available: ${data.available}",
                        modifier = Modifier
                            .background(color = LightGreen, shape = RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    
                    IconButton(
                        onClick = { onEdit(data.inventoryItemId) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit, 
                            contentDescription = "Edit", 
                            tint = Teal,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Updated At
                Text(
                    text = "Updated: ${formatIsoDate(data.updatedAt)}",
                    fontSize = 11.sp,
                    color = DarkestGray.copy(alpha = 0.6f)
                )
            }
        }
    }
}

