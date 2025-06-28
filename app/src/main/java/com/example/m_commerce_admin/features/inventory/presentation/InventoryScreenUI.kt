package com.example.m_commerce_admin.features.inventory.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.m_commerce_admin.config.theme.Teal
import com.example.m_commerce_admin.core.shared.components.states.Empty
import com.example.m_commerce_admin.core.shared.components.states.Failed
import com.example.m_commerce_admin.features.inventory.presentation.component.AdjustInventorySheet
import com.example.m_commerce_admin.features.inventory.presentation.component.InventoryCard
import com.example.m_commerce_admin.features.inventory.presentation.component.InventorySearchBar
import com.example.m_commerce_admin.features.inventory.presentation.state.InventoryLevelsState
import com.example.m_commerce_admin.features.inventory.presentation.viewModel.InventoryFilter
import com.example.m_commerce_admin.features.inventory.presentation.viewModel.InventoryViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreenUI(
    viewModel: InventoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var selectedItemId by remember { mutableStateOf<Long?>(null) }
    var isSheetVisible by remember { mutableStateOf(false) }


    AdjustInventorySheet(
        visible = isSheetVisible,
        inventoryItemId = selectedItemId,
        onConfirm = { itemId, adjustment ->
            viewModel.adjustInventoryLevel(itemId, adjustment)
        },
        onDismiss = {
            isSheetVisible = false
            selectedItemId = null
        }
    )


    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search and Filter Bar
            InventorySearchBar(
                searchQuery = searchQuery,
                selectedFilter = selectedFilter,
                onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                onFilterChange = { viewModel.updateFilter(it) },
                onClearFilters = { viewModel.clearFilters() }
            )

            // Content
            Box(modifier = Modifier.fillMaxSize()) {
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
                        val inventoryList =
                            (uiState as InventoryLevelsState.Success).inventoryLevels

                        if (inventoryList.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Empty(
                                    if (searchQuery.isNotEmpty() || selectedFilter != InventoryFilter.ALL) {
                                        "No items match your search criteria."
                                    } else {
                                        "No inventory items found."
                                    }
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(inventoryList) { item ->
                                    InventoryCard(
                                        data = item,
                                        onEdit = {
                                            selectedItemId = it
                                            isSheetVisible = true
                                        }
                                    )
                                }
                            }
                        }
                    }

                    is InventoryLevelsState.Error -> {
                        Failed("Something Went Wrong!")
                    }

                    InventoryLevelsState.Empty -> {
                        Empty(
                            if (searchQuery.isNotEmpty() || selectedFilter != InventoryFilter.ALL) {
                                "No items match your search criteria."
                            } else {
                                "No inventory levels found for the specified locations."
                            }
                        )
                    }
                }
            }
        }
    }
}
