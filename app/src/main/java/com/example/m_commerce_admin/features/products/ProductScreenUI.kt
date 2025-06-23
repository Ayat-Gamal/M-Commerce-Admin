package com.example.m_commerce_admin.features.products

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.m_commerce_admin.R
import com.example.m_commerce_admin.config.routes.AppRoutes
import com.example.m_commerce_admin.config.theme.Teal
import com.example.m_commerce_admin.core.shared.components.states.Failed
import com.example.m_commerce_admin.features.home.presentation.HomeState
import com.example.m_commerce_admin.features.home.presentation.component.LastOrdersCard
import com.example.m_commerce_admin.features.products.presentation.ProductState
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
        viewModel.loadInitialProducts()
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisible ->
                val total = listState.layoutInfo.totalItemsCount
                if (lastVisible == total - 1) {
                    viewModel.loadMoreProducts()
                }
            }
    }

    Scaffold { pad ->
        Box(modifier = Modifier.padding(pad).fillMaxSize()) {

            when (state) {
                is ProductState.Loading -> {
                     Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Teal)
                    }
                }

                is ProductState.Error -> {
                    val msg = (state as ProductState.Error).message
                    Text("Error: $msg", modifier = Modifier.align(Alignment.Center))
                }

                is ProductState.Success -> {
                    val products = (state as ProductState.Success).data
                    val hasNext = (state as ProductState.Success).hasNext

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
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .align(Alignment.Center),
                                    color = Teal
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
