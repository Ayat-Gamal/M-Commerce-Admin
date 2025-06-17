package com.example.m_commerce_admin.features.products.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.m_commerce_admin.config.theme.DarkestGray
import com.example.m_commerce_admin.config.theme.LightGreen
import com.example.m_commerce_admin.config.theme.LightTeal
import com.example.m_commerce_admin.config.theme.Teal
import com.example.m_commerce_admin.config.theme.lightRed
import com.example.m_commerce_admin.core.helpers.formatCreatedAt
import com.example.m_commerce_admin.core.shared.components.PngImage
import com.example.m_commerce_admin.features.products.ProductObject


@Composable
fun ProductCard(product: ProductObject) {
    Card(
        colors = CardDefaults.cardColors(containerColor = LightTeal),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .border(BorderStroke(1.dp, Teal), RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PngImage(
                product.image,
                modifier = Modifier.size(80.dp),
                contentDescription = product.title,
            )

            Spacer(modifier = Modifier.size(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {

                Text(product.title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Text("EGP ${product.price}", fontSize = 14.sp, color = Color.DarkGray)
                Text("In Stock: ${product.quantity}", fontSize = 12.sp)

                val statusColor = if (product.status == "Active") LightGreen else lightRed
                Text(
                    product.status,
                    fontSize = 12.sp,
                    color = Color.White,
                    modifier = Modifier
                        .background(statusColor, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                )
                val createdDate = formatCreatedAt(product.createdAt)
                Text("Created: $createdDate", fontSize = 12.sp, color = DarkestGray)
            }

            Column {
                IconButton(onClick = { }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Teal)
                }
                IconButton(onClick = { }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                }
            }
        }
    }

}
