package com.example.m_commerce_admin.features.home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.m_commerce_admin.config.routes.AppRoutes
import com.example.m_commerce_admin.config.theme.Teal
import com.example.m_commerce_admin.features.home.presentation.component.LastOrdersCard
import com.example.m_commerce_admin.features.home.presentation.viewModel.HomeViewModel

data class CardObject(val title: String, val value: Int, val icon: Int, val description: String)

@Composable
fun HomeScreenUI(
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavController
) {
    val state by viewModel.orderState.collectAsState()

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
                    }
                }
            }
        }
    }

}


