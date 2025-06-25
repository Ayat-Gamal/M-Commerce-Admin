package com.example.m_commerce_admin.features.products

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.m_commerce_admin.config.theme.Teal
import com.example.m_commerce_admin.core.shared.components.states.Failed
import com.example.m_commerce_admin.features.products.presentation.component.ProductCard
import com.example.m_commerce_admin.features.products.presentation.states.GetProductState
import com.example.m_commerce_admin.features.products.presentation.viewModel.ProductsViewModel
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProductScreenUI(
    viewModel: ProductsViewModel = hiltViewModel()
) {
    val state by viewModel.productsState.collectAsState()
    val listState = rememberLazyListState()
    val deleteState by viewModel.deleteProductState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()


    LaunchedEffect(Unit) {
        viewModel.refreshProducts()
    }

    val shouldLoadMore by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItemsCount = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

            lastVisibleItemIndex >= totalItemsCount - 3 && totalItemsCount > 0
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            viewModel.loadMoreProducts()
        }
    }

    LaunchedEffect(deleteState) {
        if (deleteState != null && deleteState!!.isSuccess) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Product deleted successfully")
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { pad ->
        Box(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
        ) {
            when (state) {
                is GetProductState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Teal)
                    }
                }

                is GetProductState.Error -> {
                    val msg = (state as GetProductState.Error).message
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Failed("Something Went Wrong! \n $msg")
                    }
                }

                is GetProductState.Success -> {
                    val products = (state as GetProductState.Success).data
                    val hasNext = (state as GetProductState.Success).hasNext


                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(products) { product ->
                            ProductCard(product = product,
                                onDelete = { viewModel.deleteProduct(productId = product.id) },
                                onEdit = {}
                            )


                        }

                        if (hasNext) {
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Products (${products.size})",
                                        color = Teal
                                    )
                                    IconButton(
                                        onClick = {
                                            viewModel.refreshProducts()
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Refresh,
                                            contentDescription = "Refresh",
                                            tint = Teal
                                        )
                                    }
                                }
                            }

                            items(products) { product ->
                                ProductCard(
                                    product = product,
                                    onEdit = { /* TODO: Edit logic */ },
                                    onDelete = {

                                        viewModel.deleteProduct(product.id)
                                    }
                                )
                            }

                            if (hasNext) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(color = Teal)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

