package com.example.m_commerce_admin.features.products.presentation.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
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
import androidx.compose.runtime.remember
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
import com.example.m_commerce_admin.features.products.domain.entity.rest.RestProduct

private fun String.capitalize(): String {
    return if (this.isNotEmpty()) {
        this[0].uppercase() + this.substring(1).lowercase()
    } else {
        this
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RestProductCard(
    product: RestProduct,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val firstVariant = remember(product) { product.variants}
    val firstImage = remember(product) { product.images?.firstOrNull() }
    val imageCount = product.images?.size ?: 0
    val createdDate = product.createdAt?.let { formatIsoDate(it) } ?: "Unknown"
    val status = product.status?.replaceFirstChar { it.uppercase() } ?: "Unknown"
    val statusColor = when (product.status?.lowercase()) {
        "active" -> LightGreen
        "draft" -> LightTeal
        "archived" -> lightRed
        else -> DarkGray
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Image
            if (firstImage == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(LightTeal, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No Image", color = DarkGray, fontSize = 14.sp)
                }
            } else {
                NetworkImage(
                    url = firstImage.src,
                    contentDescription = firstImage.alt ?: "Product Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(White, RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            // Title
            Text(product.title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DarkestGray)

            // Type + Vendor
            if (!product.productType.isNullOrBlank()) {
                Text("Type: ${product.productType}", fontSize = 12.sp, color = DarkGray)
            }
            if (!product.vendor.isNullOrBlank()) {
                Text("Vendor: ${product.vendor}", fontSize = 12.sp, color = DarkGray)
            }

            // Price, Stock, Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    if (firstVariant != null) {
                        Text(
                            "EGP ${firstVariant.first().price}",
                            fontSize = 14.sp,
                            color = Teal,
                            fontWeight = FontWeight.Medium
                        )
                        Text("Stock: ${firstVariant.first().quantity}", fontSize = 13.sp, color = DarkGray)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        status,
                        color = White,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .background(statusColor, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                    if (imageCount > 0) {
                        Text(
                            "$imageCount image${if (imageCount > 1) "s" else ""}",
                            fontSize = 11.sp,
                            color = DarkGray
                        )
                    }
                }
            }

            // Variant and SKU info
            if (product.variants.size > 1) {
                Text("${product.variants.size} variants", fontSize = 12.sp, color = DarkGray)
            }
            if (firstVariant.first().sku.isNotBlank()) {
                Text("SKU: ${firstVariant!!.first().sku}", fontSize = 12.sp, color = DarkGray)
            }

            // Created Date
            Text("Created: $createdDate", fontSize = 12.sp, color = DarkestGray)

            // Action buttons
            Row(modifier = Modifier.fillMaxWidth()) {
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
