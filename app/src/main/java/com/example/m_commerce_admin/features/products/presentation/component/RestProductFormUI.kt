package com.example.m_commerce_admin.features.products.presentation.component

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import com.example.m_commerce_admin.features.products.domain.entity.rest.RestProductOptionInput
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
    var productTypeOptions = listOf("T-SHIRTS", "SHOES", "ACCESSORIES", "Other")
    var vendorOptions = listOf("NIKE", "ADIDAS", "VANS","PUMA","FLEX FIT","ASICS TIGER","LOCAL")

    var selectedProductType by remember { mutableStateOf("") }
    var customProductType by remember { mutableStateOf("") }

    var selectedVendor by remember { mutableStateOf("") }
    var customVendor by remember { mutableStateOf("") }


    var variantList by remember {
        mutableStateOf(
            if (isEditMode && productToEdit != null) {
                productToEdit.variants.map {
                    RestProductVariantInput(
                        option1 = "Size",
                        price = it.price,
                        sku = it.sku,
                        inventoryQuantity = it.quantity
                    )
                }
            } else {
                listOf(RestProductVariantInput(option1 = "Size",price = ""))
            }
        )
    }

    var singleProductPrice by remember { mutableStateOf("") }
    var singleProductSku by remember { mutableStateOf("") }
    var singleProductQuantity by remember { mutableStateOf("") }



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
    val hasOnlySize = variantList.all { !it.option1.isNullOrBlank() && it.option2.isNullOrBlank() }
    val hasOnlyColor = variantList.all { it.option1.isNullOrBlank() && !it.option2.isNullOrBlank() }
    val hasSizeAndColor = variantList.all { !it.option1.isNullOrBlank() && !it.option2.isNullOrBlank() }

    val normalizedVariants = variantList.map {
        val size = it.option1?.takeIf { it.isNotBlank() } ?: "Default"
        val color = it.option2?.takeIf { it.isNotBlank() }

        when {
            hasOnlySize -> it.copy(option1 = size, option2 = null)
            hasOnlyColor -> it.copy(option1 = color ?: "Default", option2 = null)
            hasSizeAndColor -> it.copy(option1 = size, option2 = color ?: "Color")
            else -> it.copy(option1 = "Default", option2 = null)
        }
    }


// Generate the correct options
    val options = mutableListOf<RestProductOptionInput>()
    var position = 1

    if (hasOnlySize || hasSizeAndColor) {
        val sizeValues = normalizedVariants.mapNotNull { it.option1 }.distinct()
        options.add(RestProductOptionInput("Size", position++, sizeValues))
    }
    if (hasOnlyColor || hasSizeAndColor) {
        val colorValues = normalizedVariants.mapNotNull { it.option2 }.distinct()
        options.add(RestProductOptionInput("Color", position++, colorValues))
    }

    // Determine current state based on mode
    val currentState = if (isEditMode) updateProductState else addProductState
    val isLoading = currentState is AddRestProductState.Loading || currentState is UpdateRestProductState.Loading
    val finalProductType = if (selectedProductType == "Other") customProductType else selectedProductType
    val finalVendor = if (selectedVendor == "Other") customVendor else selectedVendor

    LaunchedEffect(title, description, productTypeOptions, vendorOptions, variantList) {
        val allVariantsValid = variantList.all { it.price.toDoubleOrNull()?.let { p -> p > 0 } ?: false }
        isFormValid = title.isNotBlank() && description.isNotBlank() &&
                productTypeOptions.isNotEmpty() && vendorOptions.isNotEmpty() && allVariantsValid
    }


    LaunchedEffect(addProductState) {
        when (addProductState) {
            is AddRestProductState.Success -> {
                // Reset form
                title = ""
                description = ""
                productTypeOptions = listOf(" ")
                vendorOptions = listOf(" ")
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

                    // Product Type Dropdown
                    DropdownWithCustomInput(
                        label = "Product Type",
                        options = productTypeOptions,
                        selectedOption = selectedProductType,
                        onOptionSelected = { selectedProductType = it },
                        customInput = customProductType,
                        onCustomInputChange = { customProductType = it }
                    )

                    // Vendor Dropdown
                    DropdownWithCustomInput(
                        label = "Vendor",
                        options = vendorOptions,
                        selectedOption = selectedVendor,
                        onOptionSelected = { selectedVendor = it },
                        customInput = customVendor,
                        onCustomInputChange = { customVendor = it }
                    )

                }

            }
            var hasVariants by remember { mutableStateOf(variantList.size > 1) }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = hasVariants,
                    onCheckedChange = {
                        hasVariants = it
                        if (!it) {
                            variantList = listOf(RestProductVariantInput(option1 = "Default", price = ""))
                        } else {
                            variantList = listOf(
                                RestProductVariantInput(option1 = "Size", price = ""),
                                RestProductVariantInput(option1 = "Color", price = "")
                            )
                        }
                    }
                )
                Text("This product has variants", fontSize = 14.sp)

            }
            if (!hasVariants) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(Color(0xFFF8F9FA))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Product Info",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = singleProductPrice,
                            onValueChange = { singleProductPrice = it },
                            label = { Text("Price") },
                            placeholder = { Text("e.g., 99.99") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = singleProductSku,
                            onValueChange = { singleProductSku = it },
                            label = { Text("SKU (Optional)") },
                            placeholder = { Text("e.g., SKU001") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = singleProductQuantity,
                            onValueChange = { singleProductQuantity = it },
                            label = { Text("Inventory Quantity") },
                            placeholder = { Text("e.g., 50") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            if (hasVariants) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(Color(0xFFF8F9FA))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Product Variants",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        VariantListSection(
                            variants = variantList,
                            onUpdateVariant = { index, updated ->
                                variantList = variantList.toMutableList().also { it[index] = updated }
                            },
                            onAddVariant = {
                                variantList = variantList + RestProductVariantInput(option1 = "Size", price = "")
                            },
                            onRemoveVariant = { index ->
                                variantList = variantList.toMutableList().also { it.removeAt(index) }
                            }
                        )
                    }
                }
                Text(
                    text = "Tip: You can fill Size only, Color only, or both. Leave one empty if not applicable.",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

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
            if (!hasVariants) {
                val singleVariant = RestProductVariantInput(
                    option1 = "Defalut",
                    price = singleProductPrice,
                    sku = singleProductSku,
                    inventoryQuantity = singleProductQuantity.toIntOrNull() ?: 0
                )
                variantList = listOf(singleVariant)
            }

            // Submit Button
            Button(
                onClick = {
                    if (isEditMode && productToEdit != null) {
                        val updateInput = RestProductUpdateInput(
                            title = title,
                            productType = finalProductType,
                            vendor = finalVendor,

                            status = selectedStatus,
                            variants = productToEdit.variants.mapIndexedNotNull { index, existing ->
                                variantList.getOrNull(index)?.let {
                                    RestProductVariantUpdateInput(
                                        id = existing.id,
                                        price = it.price,
                                        sku = it.sku,
                                        title = it.title,
                                        inventoryManagement = it.inventoryManagement,
                                        inventoryPolicy = it.inventoryPolicy,
                                        fulfillmentService = it.fulfillmentService,
                                        weight = it.weight,
                                        weightUnit = it.weightUnit,
                                        barcode = it.barcode,
                                        taxable = it.taxable,
                                        requiresShipping = it.requiresShipping
                                    )
                                }
                            }
                        )
                        viewModel.updateProduct(productToEdit.id, updateInput)
                    } else {

                        val productInput = RestProductInput(
                            title = title,
                            descriptionHtml = description,
                            productType = finalProductType,
                            vendor = finalVendor,
                            status = selectedStatus,
                            variants = variantList,
                            options = options,

                            )

                        viewModel.addProduct(productInput, selectedImages, context)
                    }
                }
                ,
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
                    Log.d("ProductForm", "Submitting with variants: $variantList")

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