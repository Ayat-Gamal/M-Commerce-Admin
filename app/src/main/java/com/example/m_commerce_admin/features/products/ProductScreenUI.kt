package com.example.m_commerce_admin.features.products

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.m_commerce_admin.config.theme.Teal
import com.example.m_commerce_admin.core.shared.components.states.Empty
import com.example.m_commerce_admin.core.shared.components.states.Failed
import com.example.m_commerce_admin.features.inventory.presentation.viewModel.InventoryViewModel
import com.example.m_commerce_admin.features.products.domain.entity.rest.RestProduct
import com.example.m_commerce_admin.features.products.presentation.component.ProductSearchBar
import com.example.m_commerce_admin.features.products.presentation.component.RestProductCard
import com.example.m_commerce_admin.features.products.presentation.component.RestProductFormUI
import com.example.m_commerce_admin.features.products.presentation.viewModel.DeleteRestProductState
import com.example.m_commerce_admin.features.products.presentation.viewModel.RestProductsState
import com.example.m_commerce_admin.features.products.presentation.viewModel.RestProductsViewModel
import kotlinx.coroutines.coroutineScope

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProductScreenUI(
    viewModel: RestProductsViewModel = hiltViewModel(),
) {
    val state by viewModel.productsState.collectAsState()
    val deleteState by viewModel.deleteProductState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedStatus by viewModel.selectedStatus.collectAsState()
    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }


    // State for edit mode
    var showEditForm by remember { mutableStateOf(false) }
    var productToEdit by remember { mutableStateOf<RestProduct?>(null) }

    val shouldLoadMore by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItemsCount = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

            lastVisibleItemIndex >= totalItemsCount - 3 && totalItemsCount > 0
        }
    }
    val context = LocalContext.current
//
//    LaunchedEffect(Unit) {
//       coroutineScope {
//
//        viewModel.testAddProductWithVariants(context = context)
//       }
//    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            viewModel.loadMoreProducts()
        }
    }

    // Handle delete state
    LaunchedEffect(deleteState) {
        when (deleteState) {
            is DeleteRestProductState.Success -> {
                snackbarHostState.showSnackbar("Product deleted successfully!")
                viewModel.resetDeleteProductState()
            }

            is DeleteRestProductState.Error -> {
                snackbarHostState.showSnackbar("Failed to delete product: ${(deleteState as DeleteRestProductState.Error).message}")
                viewModel.resetDeleteProductState()
            }

            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { pad ->
        if (showEditForm && productToEdit != null) {
            // Show edit form in full screen
            RestProductFormUI(
                viewModel = viewModel,
                navController = null,
                isEditMode = true,
                productToEdit = productToEdit,
                onBackPressed = {
                    showEditForm = false
                    productToEdit = null
                }
            )
        } else {

            Column(
                modifier = Modifier
                    .padding(pad)
                    .fillMaxSize()
            ) {
                // Search and Filter Bar
                ProductSearchBar(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                    selectedStatus = selectedStatus,
                    onStatusChange = { viewModel.updateStatusFilter(it) }
                )

                // Main Content
                Box(modifier = Modifier.fillMaxSize()) {
                    when (state) {
                        is RestProductsState.Loading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = Teal)
                            }
                        }

                        is RestProductsState.Error -> {
                            val msg = (state as RestProductsState.Error).message
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Failed("Something Went Wrong! \n $msg")
                            }
                        }

                        is RestProductsState.Success -> {
                            val products = (state as RestProductsState.Success).products

                            if (products.isEmpty()) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Empty("No products found")
                                }
                            } else {
                                LazyColumn(
                                    state = listState,
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    // Header with product count and refresh button
                                    item {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(
                                                    text = "Products (${products.size})",
                                                    color = Teal,
                                                    fontSize = 18.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                if (searchQuery.isNotEmpty() || selectedStatus != null) {
                                                    Text(
                                                        text = "Filtered results",
                                                        fontSize = 12.sp,
                                                        color = Teal.copy(alpha = 0.7f)
                                                    )
                                                }
                                            }
                                            IconButton(
                                                onClick = { viewModel.refreshProducts() }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Refresh,
                                                    contentDescription = "Refresh",
                                                    tint = Teal
                                                )
                                            }
                                        }
                                    }
                                    // Product items
                                    items(products) { product ->
                                        RestProductCard(
                                            product = product,
                                            onEdit = {
                                                productToEdit = product
                                                showEditForm = true
                                            },
                                            onDelete = {
                                                viewModel.deleteProduct(product.id)
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        RestProductsState.Idle -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Empty("Loading products...")
                            }
                        }
                    }
                }
            }
        }
    }
}




