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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.m_commerce_admin.R
import com.example.m_commerce_admin.config.theme.DarkestGray
import com.example.m_commerce_admin.config.theme.Gray
import com.example.m_commerce_admin.config.theme.Teal
import com.example.m_commerce_admin.config.theme.White
import com.example.m_commerce_admin.core.shared.components.SvgImage
import com.example.m_commerce_admin.core.shared.components.states.Empty
import com.example.m_commerce_admin.features.home.domain.entity.Order

@Composable
fun EnhancedOrdersCard(
    orders: List<Order>,
    onViewAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (orders.isEmpty()) {
        Empty("No orders available")
        return
    }

    // Take up to 3 recent orders for display
    val recentOrders = orders.take(3)
    val remainingCount = orders.size - recentOrders.size

    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .background(color = White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
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
                    text = "Recent Orders",
                    style = TextStyle(
                        fontSize = 20.sp,
                        color = Teal,
                        fontWeight = FontWeight.Bold
                    ),
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Orders count badge
                Text(
                    text = "${orders.size} total",
                    style = MaterialTheme.typography.labelSmall,
                    color = Gray,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Orders List
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                recentOrders.forEach { order ->
                    OrderItemCard(order = order)
                }
            }

            // Show remaining count if there are more orders
            if (remainingCount > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "+$remainingCount more orders",
                        style = MaterialTheme.typography.bodySmall,
                        color = Gray,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            Divider(color = Gray.copy(alpha = 0.3f))
            
            Spacer(modifier = Modifier.height(12.dp))

            // View All Button
            Button(
                onClick = onViewAllClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonColors(
                    contentColor = White,
                    containerColor = Teal,
                    disabledContainerColor = Gray,
                    disabledContentColor = White
                )
            ) {
                Text(
                    text = "View All Orders",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.padding(4.dp))
                
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "View All",
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
} 