package com.example.m_commerce_admin.features.home.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.m_commerce_admin.R
import com.example.m_commerce_admin.config.routes.AppRoutes
import com.example.m_commerce_admin.config.theme.Teal
import com.example.m_commerce_admin.config.theme.White
import com.example.m_commerce_admin.core.shared.components.SvgImage
import com.example.m_commerce_admin.features.home.domain.entity.Order
import com.example.m_commerce_admin.features.inventory.domain.entity.InventoryLevel


@Composable
fun HomeContent(
    orders: List<Order>,
    stockLevel: List<InventoryLevel>?,
    navController: NavController
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                HorizontalDivider(
                    color = Teal,
                    thickness = 2.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 16.dp)
                )
            }
        }
        item {
            EnhancedOrdersCard(
                orders = orders,
                onViewAllClick = {
                    navController.navigate(AppRoutes.OrdersScreen)
                }
            )
        }
        item {
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
                            resId = R.raw.inventory,
                            contentDescription = "Inventory Level ",
                            modifier = Modifier
                                .size(24.dp)
                                .padding(end = 8.dp),
                            colorFilter = ColorFilter.tint(Teal)
                        )
                        Text(
                            text = "Inventory Level ",
                            style = TextStyle(
                                fontSize = 20.sp,
                                color = Teal
                            ),
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = "${stockLevel?.size ?: 0} Products below 5 in stock ",
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = Red
                        ),
                    )
                }
            }
        }
        item {
            Card(
                elevation = CardDefaults.cardElevation(4.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp)
                    .background(color = White)
                    .padding(bottom =38.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Header Row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        SvgImage(
                            resId = R.raw.coupon,
                            contentDescription = "Quick Access",
                            modifier = Modifier
                                .size(24.dp)
                                .padding(end = 8.dp),
                            colorFilter = ColorFilter.tint(Teal)
                        )
                        Text(
                            text = "Quick Access",
                            style = TextStyle(
                                fontSize = 20.sp,
                                color = Teal
                            ),
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    QuickAccessButtons(
                        onAddProductClick = {
                            navController.navigate(AppRoutes.RestProductForm)
                        },
                        onAddCouponClick = {
                            navController.navigate(AppRoutes.AddCouponForm)
                        }
                    )
                }
            }
        }
    }
}

