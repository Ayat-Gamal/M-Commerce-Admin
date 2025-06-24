package com.example.m_commerce_admin.features.products.presentation.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.m_commerce_admin.config.theme.DarkestGray
import com.example.m_commerce_admin.config.theme.LightGreen
import com.example.m_commerce_admin.config.theme.LightTeal
import com.example.m_commerce_admin.config.theme.Teal
import com.example.m_commerce_admin.config.theme.White
import com.example.m_commerce_admin.config.theme.lightRed
import com.example.m_commerce_admin.core.helpers.formatIsoDate
import com.example.m_commerce_admin.core.shared.components.NetworkImage
import com.example.m_commerce_admin.features.products.domain.entity.Product

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProductCard(
    product: Product,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = LightTeal),
        elevation = CardDefaults.cardElevation(6.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .border(BorderStroke(1.dp, Teal), RoundedCornerShape(16.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Larger Image on Top
            val imageUrl = product.featuredImage ?: product.images.firstOrNull()
            
            if (imageUrl.isNullOrEmpty()) {
                // Show placeholder when no image is available
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(White, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No Image",
                        color = DarkGray,
                        fontSize = 14.sp
                    )
                }
            } else {
                NetworkImage(
                    url = imageUrl,
                    contentDescription = "Product image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(White, RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(product.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)

                // Essential admin information
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        // Price and inventory - critical for admin
                        if (product.price != "0.00") {
                            Text("EGP ${product.price}", fontSize = 14.sp, color = DarkGray, fontWeight = FontWeight.Medium)
                        }
                        Text("Stock: ${product.inventoryQuantity}", fontSize = 13.sp, color = DarkGray)
                    }
                    
                    Column(horizontalAlignment = Alignment.End) {
                        // Status and image count
                        val statusColor = if (product.status.equals("Active", true)) LightGreen else lightRed
                        Text(
                            text = product.status,
                            color = White,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .background(statusColor, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                        
                        if (product.images.size > 1) {
                            Text("${product.images.size} images", fontSize = 11.sp, color = DarkGray)
                        }
                    }
                }

                // Additional admin details
                if (product.sku.isNotBlank()) {
                    Text("SKU: ${product.sku}", fontSize = 12.sp, color = DarkGray)
                }

                val createdDate = formatIsoDate(product.createdAt)
                Text("Created: $createdDate", fontSize = 12.sp, color = DarkestGray)
            }

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Teal)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = lightRed)
                }
            }
        }
    }
}
