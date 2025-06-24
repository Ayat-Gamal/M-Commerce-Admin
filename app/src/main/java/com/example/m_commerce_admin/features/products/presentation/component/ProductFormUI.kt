package com.example.m_commerce_admin.features.products.presentation.component

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.m_commerce_admin.config.theme.Teal
import com.example.m_commerce_admin.core.shared.components.ImagePicker
import com.example.m_commerce_admin.features.products.domain.entity.DomainProductInput
import com.example.m_commerce_admin.features.products.domain.entity.ProductImage
import com.example.m_commerce_admin.features.products.domain.entity.ProductStatus
import com.example.m_commerce_admin.features.products.presentation.states.AddProductState
import com.example.m_commerce_admin.features.products.presentation.viewModel.ProductsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormUI(
    viewModel: ProductsViewModel = hiltViewModel(),
    navController: NavController? = null
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var productType by remember { mutableStateOf("") }
    var vendor by remember { mutableStateOf("") }
    var selectedImages by remember { mutableStateOf(listOf<Uri>()) }
    var isFormValid by remember { mutableStateOf(false) }
    
    val state by viewModel.uiAddProductState.collectAsState()
    val scrollState = rememberScrollState()

    // Validate form
    LaunchedEffect(title, description, productType, vendor) {
        isFormValid = title.isNotBlank() && description.isNotBlank() && 
                     productType.isNotBlank() && vendor.isNotBlank()
    }

    // Handle success state
    LaunchedEffect(state) {
        if (state is AddProductState.Success) {
            // Reset form on success
            title = ""
            description = ""
            productType = ""
            vendor = ""
            selectedImages = emptyList()
            
            // Navigate back after a short delay
            navController?.let {
                kotlinx.coroutines.delay(2000)
                it.popBackStack()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Product", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Teal,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title Field
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Product Title *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = title.isBlank() && title.isNotEmpty()
            )

            // Description Field
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Product Description *") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5,
                isError = description.isBlank() && description.isNotEmpty()
            )

            // Product Type Field
            OutlinedTextField(
                value = productType,
                onValueChange = { productType = it },
                label = { Text("Product Type *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = productType.isBlank() && productType.isNotEmpty()
            )

            // Vendor Field
            OutlinedTextField(
                value = vendor,
                onValueChange = { vendor = it },
                label = { Text("Vendor *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = vendor.isBlank() && vendor.isNotEmpty()
            )

            // Image picker
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Product Images",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Add images to showcase your product (optional)",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    ImagePicker(
                        selectedImages = selectedImages,
                        onImagesSelected = { selectedImages = it }
                    )
                }
            }

            // Status Selection
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Product Status",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ProductStatus.values().forEach { status ->
                            FilterChip(
                                onClick = { /* Status will be set to ACTIVE by default */ },
                                label = { Text(status.name) },
                                selected = status == ProductStatus.ACTIVE,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            val context = LocalContext.current

// Submit Button
            Button(
                onClick = {
                    val input = DomainProductInput(
                        title = title,
                        descriptionHtml = description,
                        productType = productType,
                        vendor = vendor,
                        status = ProductStatus.ACTIVE,
                        images = selectedImages.map { uri ->
                            ProductImage(
                                uri = uri.toString(),
                                fileName = uri.lastPathSegment ?: "image.jpg",
                                mimeType = "image/jpeg"
                            )
                        }
                    )

                    // ✅ Call the ViewModel with proper Uri list + context
                    viewModel.addProductWithImages(input, selectedImages, context)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = isFormValid && state !is AddProductState.Loading
            ) {
                if (state is AddProductState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text("Add Product", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }


            // Status Messages
            when (state) {
                is AddProductState.Success -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E8))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "✅ Product added successfully!",
                                color = Color(0xFF2E7D32),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
                is AddProductState.Error -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "❌ Error: ${(state as AddProductState.Error).message}",
                                color = Color(0xFFC62828),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
                else -> {}
            }
        }
    }
}
