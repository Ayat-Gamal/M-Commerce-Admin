package com.example.m_commerce_admin.features.home.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.m_commerce_admin.config.theme.Teal
import com.example.m_commerce_admin.core.shared.components.states.Empty
import com.example.m_commerce_admin.core.shared.components.states.Failed
import com.example.m_commerce_admin.core.shared.components.states.NoNetwork
import com.example.m_commerce_admin.features.home.presentation.component.HomeContent
import com.example.m_commerce_admin.features.home.presentation.state.HomeState
import com.example.m_commerce_admin.features.home.presentation.viewModel.HomeViewModel
import com.example.m_commerce_admin.features.inventory.presentation.viewModel.InventoryViewModel


@Composable
fun HomeScreenUI(
    viewModel: HomeViewModel = hiltViewModel(),
    inventoryViewModel: InventoryViewModel = hiltViewModel<InventoryViewModel>(),
    navController: NavController,
    isConnected: Boolean,

    ) {
    val state by viewModel.orderState.collectAsState()
    val stockLevel by inventoryViewModel.stockLevel.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchOrders()
    }

    if (!isConnected) {
        NoNetwork()

    } else {
        Scaffold { padValue ->
            Box(
                modifier = Modifier
                    .padding(padValue)
                    .systemBarsPadding()
                    .navigationBarsPadding()
                    .wrapContentHeight(),
            ) {
                when (state) {
                    is HomeState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Teal)
                        }
                    }

                    is HomeState.Error -> {
                        Failed(
                            error = (state as HomeState.Error).message
                        )
                    }

                    is HomeState.Success -> {
                        val orders = (state as HomeState.Success).data
                        HomeContent(
                            orders = orders,
                            stockLevel = stockLevel,
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}