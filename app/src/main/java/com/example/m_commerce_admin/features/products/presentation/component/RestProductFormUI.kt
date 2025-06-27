package com.example.m_commerce_admin.features.products.presentation.component

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.m_commerce_admin.config.theme.DarkestGray
import com.example.m_commerce_admin.config.theme.Teal
import com.example.m_commerce_admin.core.shared.components.ImagePicker
import com.example.m_commerce_admin.features.products.domain.entity.rest.RestProduct
import com.example.m_commerce_admin.features.products.domain.entity.rest.RestProductInput
import com.example.m_commerce_admin.features.products.domain.entity.rest.RestProductUpdateInput
import com.example.m_commerce_admin.features.products.domain.entity.rest.RestProductVariantInput
import com.example.m_commerce_admin.features.products.domain.entity.rest.RestProductVariantUpdateInput
import com.example.m_commerce_admin.features.products.presentation.viewModel.AddRestProductState
import com.example.m_commerce_admin.features.products.presentation.viewModel.RestProductsViewModel
import com.example.m_commerce_admin.features.products.presentation.viewModel.UpdateRestProductState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestProductFormUI(
    viewModel: RestProductsViewModel = hiltViewModel(),
    navController: NavController? = null,
    // Edit mode parameters
    isEditMode: Boolean = false,
    productToEdit: RestProduct? = null,
    onBackPressed: (() -> Unit)? = null
) {
    // Initialize form fields based on mode
    var title by remember { 
        mutableStateOf(if (isEditMode && productToEdit != null) productToEdit.title else "") 
    }
    var description by remember { 
        mutableStateOf(if (isEditMode && productToEdit != null) productToEdit.descriptionHtml ?: "" else "") 
    }
    var productType by remember { 
        mutableStateOf(if (isEditMode && productToEdit != null) productToEdit.productType ?: "" else "") 
    }
    var vendor by remember { 
        mutableStateOf(if (isEditMode && productToEdit != null) productToEdit.vendor ?: "" else "") 
    }
    var price by remember { 
        mutableStateOf(if (isEditMode && productToEdit != null) productToEdit.variants.firstOrNull()?.price ?: "" else "") 
    }
    var sku by remember { 
        mutableStateOf(if (isEditMode && productToEdit != null) productToEdit.variants.firstOrNull()?.sku ?: "" else "") 
    }
    var stockQuantity by remember { 
        mutableStateOf(if (isEditMode && productToEdit != null) productToEdit.variants.firstOrNull()?.quantity?.toString() ?: "0" else "0") 
    }
    var selectedStatus by remember { 
        mutableStateOf(if (isEditMode && productToEdit != null) productToEdit.status ?: "draft" else "draft") 
    }
    var selectedImages by remember { mutableStateOf(listOf<Uri>()) }
    var isFormValid by remember { mutableStateOf(false) }

    val addProductState by viewModel.addProductState.collectAsState()
    val updateProductState by viewModel.updateProductState.collectAsState()
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Determine current state based on mode
    val currentState = if (isEditMode) updateProductState else addProductState
    val isLoading = currentState is AddRestProductState.Loading || currentState is UpdateRestProductState.Loading

    LaunchedEffect(title, description, productType, vendor, price) {
        val isPriceValid = price.toDoubleOrNull()?.let { it > 0 } ?: false
        isFormValid = title.isNotBlank() && description.isNotBlank() && 
                productType.isNotBlank() && vendor.isNotBlank() && 
                price.isNotBlank() && isPriceValid
    }

    LaunchedEffect(addProductState) {
        when (addProductState) {
            is AddRestProductState.Success -> {
                // Reset form
                title = ""
                description = ""
                productType = ""
                vendor = ""
                price = ""
                sku = ""
                stockQuantity = "0"
                selectedStatus = "draft"
                selectedImages = emptyList()
                
                scope.launch {
                    snackbarHostState.showSnackbar("Product added successfully!")
                    delay(1500)
                    if (isEditMode) {
                        onBackPressed?.invoke()
                    } else {
                        navController?.popBackStack()
                    }
                }
                viewModel.resetAddProductState()
            }
            is AddRestProductState.Error -> {
                scope.launch {
                    snackbarHostState.showSnackbar("Error: ${(addProductState as AddRestProductState.Error).message}")
                }
            }
            else -> {}
        }
    }

    LaunchedEffect(updateProductState) {
        when (updateProductState) {
            is UpdateRestProductState.Success -> {
                scope.launch {
                    snackbarHostState.showSnackbar("Product updated successfully!")
                    delay(1500)
                    if (isEditMode) {
                        onBackPressed?.invoke()
                    } else {
                        navController?.popBackStack()
                    }
                }
                viewModel.resetUpdateProductState()
            }
            is UpdateRestProductState.Error -> {
                scope.launch {
                    snackbarHostState.showSnackbar("Error: ${(updateProductState as UpdateRestProductState.Error).message}")
                }
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        if (isEditMode) "Edit Product" else "Add New Product", 
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { onBackPressed?.invoke() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Teal,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState) { Snackbar(it) }
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
            // Basic Information
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(Color(0xFFF8F9FA))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Basic Information",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    FormField(
                        value = title,
                        onValueChange = { title = it },
                        label = "Product Title",
                        placeholder = "Enter product name",
                        isError = title.isBlank() && !isFormValid
                    )
                    
                    FormField(
                        value = description,
                        onValueChange = { description = it },
                        label = "Description",
                        placeholder = "Describe your product",
                        isError = description.isBlank() && !isFormValid,
                        singleLine = false
                    )
                    
                    FormField(
                        value = productType,
                        onValueChange = { productType = it },
                        label = "Product Type",
                        placeholder = "e.g., T-Shirt, Electronics",
                        isError = productType.isBlank() && !isFormValid
                    )
                    
                    FormField(
                        value = vendor,
                        onValueChange = { vendor = it },
                        label = "Vendor/Brand",
                        placeholder = "Brand name",
                        isError = vendor.isBlank() && !isFormValid
                    )
                }
            }

            // Pricing and Inventory
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(Color(0xFFF8F9FA))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Pricing & Inventory",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    FormField(
                        value = price,
                        onValueChange = {
                            if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) price = it
                        },
                        label = "Price",
                        placeholder = "0.00",
                        isError = price.isBlank() || price.toDoubleOrNull()?.let { it <= 0 } ?: true,
                        keyboardType = KeyboardType.Decimal
                    )
                    
                    FormField(
                        value = stockQuantity,
                        onValueChange = {
                            if (it.isEmpty() || it.matches(Regex("^\\d*$"))) stockQuantity = it
                        },
                        label = "Stock Quantity",
                        placeholder = "0",
                        isError = false,
                        keyboardType = KeyboardType.Number
                    )
                    
                    FormField(
                        value = sku,
                        onValueChange = { sku = it },
                        label = "SKU (Optional)",
                        placeholder = "Stock keeping unit",
                        isError = false
                    )
                }
            }

            // Product Status
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(Color(0xFFF8F9FA))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Product Status",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("draft", "active", "archived").forEach { status ->
                            FilterChip(
                                onClick = { selectedStatus = status },
                                label = { Text(status.capitalize()) },
                                selected = selectedStatus == status,
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Teal,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Product Images
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(Color(0xFFF8F9FA))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Product Images",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ImagePicker(
                        selectedImages = selectedImages,
                        onImagesSelected = { 
                            selectedImages = it
                            println("Selected ${it.size} images")
                        }
                    )
                    
                    if (selectedImages.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Images will be uploaded after product creation",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            // Submit Button
            Button(
                onClick = {
                    val variantInput = RestProductVariantInput(
                        price = price,
                        sku = sku.takeIf { it.isNotBlank() },
                        inventoryQuantity = stockQuantity.toIntOrNull() ?: 0
                    )
                    
                    if (isEditMode && productToEdit != null) {
                        // Update mode
                        val updateInput = RestProductUpdateInput(
                            title = title,
                            descriptionHtml = description,
                            productType = productType,
                            vendor = vendor,
                            status = selectedStatus,
                            variants = listOf(
                              RestProductVariantUpdateInput(
                                    id = productToEdit.variants.firstOrNull()?.id ?: 0,
                                    price = price,
                                    sku = sku.takeIf { it.isNotBlank() }
                                )
                            )
                        )
                        viewModel.updateProduct(productToEdit.id, updateInput)
                    } else {

                        val productInput = RestProductInput(
                            title = title,
                            descriptionHtml = description,
                            productType = productType,
                            vendor = vendor,
                            status = selectedStatus,
                            variants = listOf(variantInput),


                        )
                        viewModel.addProduct(productInput, selectedImages, context)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = isFormValid && !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Teal,
                    disabledContainerColor = Teal.copy(alpha = 0.6f)
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text(
                        if (isEditMode) "Update Product" else "Add Product",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun FormField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    isError: Boolean,
    singleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, style = TextStyle(color = DarkestGray)) },
        placeholder = { Text(placeholder, style = TextStyle(color = DarkestGray)) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = singleLine,
        isError = isError,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        supportingText = {
            if (isError) Text("$label is required", color = Teal)
        },
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Teal,
            unfocusedBorderColor = Color.Gray,
            focusedLabelColor = Teal
        )
    )
}

private fun String.capitalize(): String {
    return if (this.isNotEmpty()) {
        this[0].uppercase() + this.substring(1).lowercase()
    } else {
        this
    }
} 