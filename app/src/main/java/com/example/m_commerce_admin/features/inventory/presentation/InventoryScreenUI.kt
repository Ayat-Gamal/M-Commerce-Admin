package com.example.m_commerce_admin.features.inventory.presentation


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.m_commerce_admin.config.theme.Teal
import com.example.m_commerce_admin.core.shared.components.states.Empty
import com.example.m_commerce_admin.core.shared.components.states.Failed
import com.example.m_commerce_admin.features.inventory.InventoryViewModel
import com.example.m_commerce_admin.features.inventory.presentation.component.InventoryCard
import com.example.m_commerce_admin.features.inventory.presentation.state.InventoryLevelsState


@Composable
fun InventoryScreenUI(
    viewModel: InventoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(


    )
    { inn ->
        when (uiState) {
            InventoryLevelsState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Teal)
                }
            }

            is InventoryLevelsState.Success -> {
                val inventoryList = (uiState as InventoryLevelsState.Success).inventoryLevels
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        items(inventoryList) { item ->
                            InventoryCard(
                                data = item,
                                onEdit = { TODO() },
                                onDelete = { TODO() }
                            )
                        }
                    }
                }


            }

            is InventoryLevelsState.Error -> {
                 Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Failed("Error: ${(uiState as InventoryLevelsState.Error).message}")
                }
            }

            InventoryLevelsState.Empty -> {
                 Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Empty("No inventory levels found for the specified locations.")
                }
            }
        }

    }
}
