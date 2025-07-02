package com.example.m_commerce_admin.features.home.presentation.orders

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.m_commerce_admin.core.shared.components.states.Failed
import com.example.m_commerce_admin.features.home.presentation.state.HomeState
import com.example.m_commerce_admin.features.home.presentation.viewModel.HomeViewModel
import com.example.m_commerce_admin.features.home.presentation.component.OrderItemCard


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreenUI(
    viewModel: HomeViewModel = hiltViewModel(),
     onBackClick: () -> Unit,
) {
    val state by viewModel.orderState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchOrders()
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All Orders") },
                navigationIcon = {
                    Icon(Icons.Default.ArrowBack,
                        contentDescription = "back",
                        modifier = Modifier
                            .padding(16.dp)
                            .clickable { onBackClick() }
                    )
                },
            )
        }
    ) { padding ->
        when (state) {
            is HomeState.Loading -> CircularProgressIndicator()
            is HomeState.Error -> Failed("Error loading orders.")
            is HomeState.Success -> {
                val orders = (state as HomeState.Success).data
                LazyColumn(modifier = Modifier.padding(padding)) {
                    items(items = orders) {
                        OrderItemCard(it)
                    }
                }

            }
        }


    }
}
