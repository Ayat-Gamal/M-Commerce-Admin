package com.example.m_commerce_admin.features.home.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.m_commerce_admin.config.theme.DarkestGray
import com.example.m_commerce_admin.config.theme.Gray
import com.example.m_commerce_admin.config.theme.White
import com.example.m_commerce_admin.core.helpers.formatIsoDate
import com.example.m_commerce_admin.features.home.domain.entity.Order

@Composable
fun OrderItemCard(
    order: Order,
    modifier: Modifier = Modifier
) {
    val orderStatus = OrderStatus.fromString(order.status)
    
    Card(
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header Row with Order ID and Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = order.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DarkestGray
                    )
                    
                    order.createdAt?.let { date ->
                        Text(
                            text = formatIsoDate(date),
                            style = MaterialTheme.typography.bodySmall,
                            color = Gray,
                            fontSize = 12.sp
                        )
                    }
                }
                
                // Status Badge
                Text(
                    text = orderStatus.displayName,
                    style = MaterialTheme.typography.labelSmall,
                    color = White,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .background(
                            color = orderStatus.color,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Customer Information
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Customer Avatar
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Customer",
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Gray.copy(alpha = 0.2f))
                        .padding(6.dp),
                    tint = Gray
                )
                
                Spacer(modifier = Modifier.padding(8.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = order.customerName ?: "Unknown Customer",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = DarkestGray
                    )
                    
                    order.customerEmail?.let { email ->
                        Text(
                            text = email,
                            style = MaterialTheme.typography.bodySmall,
                            color = Gray,
                            fontSize = 12.sp
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Divider(color = Gray.copy(alpha = 0.3f))
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Order Amount
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total Amount",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Gray
                )
                
                Text(
                    text = "${order.totalAmount} ${order.currency}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = DarkestGray
                )
            }
        }
    }
} 