package com.example.m_commerce_admin.features.products

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.m_commerce_admin.config.theme.Teal
import com.example.m_commerce_admin.features.products.presentation.states.GetProductState
import com.example.m_commerce_admin.features.products.presentation.component.ProductCard
import com.example.m_commerce_admin.features.products.presentation.viewModel.ProductsViewModel


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProductScreenUI(
    viewModel: ProductsViewModel = hiltViewModel()
) {
    val state by viewModel.productsState.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        viewModel.loadMoreProducts()
    }

    // Improved pagination logic
    val shouldLoadMore by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItemsCount = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            
            // Load more when we're 3 items away from the end
            lastVisibleItemIndex >= totalItemsCount - 3 && totalItemsCount > 0
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            viewModel.loadMoreProducts()
        }
    }

    Scaffold { pad ->
        Box(modifier = Modifier.padding(pad).fillMaxSize()) {

            when (state) {
                is GetProductState.Loading -> {
                     Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Teal)
                    }
                }

                is GetProductState.Error -> {
                    val msg = (state as GetProductState.Error).message
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Error: $msg")
                        Button(
                            onClick = { viewModel.testConnection() },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text("Test Connection")
                        }
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
                        item {
                            Text(
                                text = "Products",
                                modifier = Modifier.padding(16.dp),
                                color = Teal
                            )
                        }

                        items(products) { product ->
                            ProductCard(product = product)
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
