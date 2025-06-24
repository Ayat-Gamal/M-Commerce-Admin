package com.example.m_commerce_admin.features.home.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.example.m_commerce_admin.R
import com.example.m_commerce_admin.config.theme.Black
import com.example.m_commerce_admin.config.theme.LightTeal
import com.example.m_commerce_admin.config.theme.Teal
import com.example.m_commerce_admin.config.theme.White
import com.example.m_commerce_admin.core.shared.components.SvgImage
import com.example.m_commerce_admin.features.home.domain.entity.Order


@Composable
fun OrderItemCard(order: Order) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = White)
    ) {
        Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                SvgImage(
                    resId = R.raw.products,
                    contentDescription = "Orders Icon",
                    modifier = Modifier
                        .size(24.dp)
                        .padding(end = 8.dp),
                    colorFilter = ColorFilter.tint(Teal)

                )
                Text(
                    text = "Order: ${order.name}",
                    color = Black
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Customer: ${order.customerName ?: "Unknown"}",
                style = MaterialTheme.typography.bodyMedium,
                color = Black
            )

            Text(
                text = "Email: ${order.customerEmail ?: "N/A"}",
                style = MaterialTheme.typography.bodyMedium,
                color = Black
            )

            Text(
                text = "Amount: ${order.totalAmount} ${order.currency}",
                style = MaterialTheme.typography.bodyMedium,
                color = Black
            )

            Text(
                text = "Status: ${order.status}",
                color = Black,
                modifier = Modifier
                    .background(color = LightTeal)
                    .background(LightTeal, RoundedCornerShape(8.dp))
                    .padding(4.dp),
            )
        }

    }
}

