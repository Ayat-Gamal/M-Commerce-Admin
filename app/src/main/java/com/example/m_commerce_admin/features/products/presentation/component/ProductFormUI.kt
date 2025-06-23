package com.example.m_commerce_admin.features.products.presentation.component

import android.net.Uri
import android.util.Log
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
import com.example.m_commerce_admin.core.shared.components.ImagePicker
import com.example.m_commerce_admin.features.products.domain.entity.DomainProductInput
import com.example.m_commerce_admin.features.products.domain.entity.ProductImage
import com.example.m_commerce_admin.features.products.domain.entity.ProductStatus
import com.example.m_commerce_admin.features.products.presentation.states.AddProductState
import com.example.m_commerce_admin.features.products.presentation.viewModel.ProductsViewModel

@Composable
fun ProductFormUI(viewModel: ProductsViewModel = hiltViewModel()) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedImages by remember { mutableStateOf(listOf<Uri>()) }
    val state by viewModel.uiAddProductState.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") })

        // Image picker
        ImagePicker(
            selectedImages = selectedImages,
            onImagesSelected = { selectedImages = it }
        )

        Button(
            onClick = {
                val input = DomainProductInput(
                    title = title,
                    descriptionHtml = description,
                    productType = "YourType",
                    vendor = "YourVendor",
                    status = ProductStatus.ACTIVE,
                    images = selectedImages.map { uri ->
                        ProductImage(
                            uri = uri.toString(),
                            fileName = uri.lastPathSegment ?: "image.jpg",
                            mimeType = "image/jpeg"
                        )
                    }
                )
                viewModel.addProductWithImages(input, selectedImages.map { it.toString() })
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Add Product")
        }

        when (state) {
            is AddProductState.Loading -> CircularProgressIndicator()
            is AddProductState.Success -> {
                Log.d("ProductScreenUI", "Rendering Success UI with products: ${selectedImages.size}")
                Text("Product added successfully!")
            }
            is AddProductState.Error -> Text("Error: ${(state as AddProductState.Error).message}")
            else -> {}
        }
    }
}
