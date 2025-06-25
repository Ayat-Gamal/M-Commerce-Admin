package com.example.m_commerce_admin.features.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.m_commerce_admin.R
import com.example.m_commerce_admin.config.routes.AppRoutes
import com.example.m_commerce_admin.config.theme.Gray
import com.example.m_commerce_admin.config.theme.Teal
import com.example.m_commerce_admin.config.theme.White
import com.example.m_commerce_admin.core.shared.components.SvgImage
import com.example.m_commerce_admin.features.home.presentation.component.LastOrdersCard
import com.example.m_commerce_admin.features.home.presentation.viewModel.HomeViewModel
import com.example.m_commerce_admin.features.inventory.InventoryViewModel


@Composable
fun HomeScreenUI(
    viewModel: HomeViewModel = hiltViewModel(),
    inventoryViewModel: InventoryViewModel = hiltViewModel<InventoryViewModel>(),
    navController: NavController
) {
    val state by viewModel.orderState.collectAsState()
    val stockLevel by inventoryViewModel.stockLevel.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchOrders()
    }

    Scaffold { padValue ->
        Box(
            modifier = Modifier
                .padding(padValue)
                .wrapContentHeight(),
        ) {
            when (state) {
                is HomeState.Loading ->

                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Teal)
                    }

                is HomeState.Error -> Text("Error: ${(state as HomeState.Error).message}")
                is HomeState.Success -> {
                    val orders = (state as HomeState.Success).data

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {

                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,


                                ) {

                            }
                            HorizontalDivider(
                                color = Teal,
                                thickness = 2.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 16.dp)
                            )
                        }

                        item {
                            LastOrdersCard(orders = orders, onViewAllClick = {
                                navController.navigate(AppRoutes.OrdersScreen)
                            })
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
                                        text = "${stockLevel?.size} Products below 5 in stock ",
                                        style = TextStyle(
                                            fontSize = 16.sp,
                                            color = Red
                                        ),
                                    )

                                }

                            }
                        }
                        item{
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
                                            navController.navigate(AppRoutes.ProductForm)
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
                }
            }
        }
    }



@Composable
fun QuickAccessButtons(
    onAddProductClick: () -> Unit,
    onAddCouponClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onAddProductClick,

                modifier = Modifier.padding(8.dp), colors = ButtonColors(
                    contentColor = White,
                    containerColor = Teal,
                    disabledContainerColor = Gray,
                    disabledContentColor = White)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Product", style = MaterialTheme.typography.bodyMedium)
            }

            Button(
                onClick = onAddCouponClick,
                modifier = Modifier.padding(8.dp), colors = ButtonColors(
                    contentColor = White,
                    containerColor = Teal,
                    disabledContainerColor = Gray,
                    disabledContentColor = White)


            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Coupon", style = MaterialTheme.typography.bodyMedium)
            }
        }

    }
}
