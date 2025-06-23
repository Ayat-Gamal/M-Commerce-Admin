package com.example.m_commerce_admin.features.products.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.apollographql.apollo.api.Optional
import com.example.m_commerce_admin.features.products.presentation.states.AddProductState
import com.example.m_commerce_admin.features.products.presentation.viewModel.ProductsViewModel
import com.example.m_commerce_admin.type.ProductInput
import com.example.m_commerce_admin.type.ProductStatus

@Composable
fun ProductFormUI(viewModel: ProductsViewModel = hiltViewModel()) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val state by viewModel.uiAddProductState.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") })

        Button(
            onClick = {
                val input = ProductInput(
                    title = Optional.presentIfNotNull(title),
                    descriptionHtml = Optional.presentIfNotNull(description),
                    productType = Optional.presentIfNotNull("YourType"),
                    vendor = Optional.presentIfNotNull("YourVendor"),
                    status = Optional.presentIfNotNull(ProductStatus.ACTIVE)
                )

                viewModel.addProduct(input)
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Add Product")
        }

        when (state) {
            is AddProductState.Loading -> CircularProgressIndicator()
            is AddProductState.Success -> Text("Product added successfully!")
            is AddProductState.Error -> Text("Error: ${(state as AddProductState.Error).message}")
            else -> {}
        }
    }
}
