package com.example.m_commerce_admin.features.home.presentation.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.m_commerce_admin.R
import com.example.m_commerce_admin.config.theme.Black
import com.example.m_commerce_admin.config.theme.Gray
import com.example.m_commerce_admin.config.theme.LightTeal
import com.example.m_commerce_admin.config.theme.Teal
import com.example.m_commerce_admin.config.theme.White
import com.example.m_commerce_admin.core.shared.components.SvgImage
import com.example.m_commerce_admin.core.shared.components.states.Empty
import com.example.m_commerce_admin.features.home.domain.entity.Order
import com.example.m_commerce_admin.features.home.presentation.CardObject

@Composable
fun LastOrdersCard(
    orders: List<Order>,
    onViewAllClick: () -> Unit
) {
    if (orders.isEmpty()) {
       Empty("Cant Retrieve Data!")
        return
    }

    val recentOrder = orders.first()

    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        modifier = Modifier
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
                    text = "Last Orders",
                    style = TextStyle(
                        fontSize = 20.sp,
                        color = Teal
                    ),
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Order: ${recentOrder.name}",
                style = MaterialTheme.typography.bodyLarge,
                color = Black
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Customer: ${recentOrder.customerName ?: "Unknown"}",
                style = MaterialTheme.typography.bodyMedium,
                color = Black
            )

            Text(
                text = "Email: ${recentOrder.customerEmail ?: "N/A"}",
                style = MaterialTheme.typography.bodyMedium,
                color = Black
            )

            Text(
                text = "Amount: ${recentOrder.totalAmount} ${recentOrder.currency}",
                style = MaterialTheme.typography.bodyMedium,
                color = Black
            )
            Spacer(modifier = Modifier.height(8.dp))

//            Text(
//                text = "Status:${recentOrder.status}",
//                color = Black,
//                modifier = Modifier
//                    .background(LightTeal, RoundedCornerShape(8.dp))
//            )

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {


                Text(
                    text = "+${orders.size - 1} more",
                    style = MaterialTheme.typography.labelMedium,
                    color = Black
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onViewAllClick,
                    modifier = Modifier.padding(8.dp), colors = ButtonColors(
                        contentColor = White,
                        containerColor = Teal,
                        disabledContainerColor = Gray,
                        disabledContentColor = White
                    )
                ) {
                    Text("View All")
                }

            }
        }
    }
}
