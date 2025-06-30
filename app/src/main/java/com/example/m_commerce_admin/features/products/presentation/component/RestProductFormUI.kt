package com.example.m_commerce_admin.features.products.presentation.component

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.m_commerce_admin.config.theme.LightGray80
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
import com.google.gson.Gson
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
        mutableStateOf(
            if (isEditMode && productToEdit != null) productToEdit.descriptionHtml ?: "" else ""
        )
    }
    var productTypeOptions = listOf("T-SHIRTS", "SHOES", "ACCESSORIES", "Other")
    var vendorOptions =
        listOf("NIKE", "ADIDAS", "VANS", "PUMA", "FLEX FIT", "ASICS TIGER", "LOCAL", "Other")

    var selectedProductType by rememberSaveable {

        mutableStateOf(if (isEditMode && productToEdit != null) productToEdit.productType else " ")
    }

    var customProductType by rememberSaveable {
        mutableStateOf(if (isEditMode && productToEdit != null) productToEdit.productType else " ")

    }

    var selectedVendor by rememberSaveable { mutableStateOf(if (isEditMode && productToEdit != null) productToEdit.vendor else " ") }
    var customVendor by rememberSaveable { mutableStateOf(if (isEditMode && productToEdit != null) productToEdit.vendor else " ") }


    var variantList by remember {
        mutableStateOf(
            if (isEditMode && productToEdit != null) {

                productToEdit.variants.map {
                    RestProductVariantInput(
                        option1 = it.title,
                        price = it.price,
                        sku = it.sku,
                        inventoryQuantity = it.quantity
                    )
                }
            } else {
                listOf(RestProductVariantInput(option1 = "Size", price = ""))
            }
        )
    }

    var singleProductPrice by rememberSaveable { mutableStateOf(if (isEditMode && productToEdit != null) productToEdit.variants.first().price else "") }
    var singleProductSku by rememberSaveable {
        mutableStateOf(if (isEditMode && productToEdit != null) productToEdit.variants.first().sku else "")
    }

    var singleProductQuantity by rememberSaveable {
        mutableStateOf(
            if (isEditMode && productToEdit != null) productToEdit.variants.firstOrNull()?.quantity
                ?: 0
            else 0
        )
    }


    var selectedStatus by remember {
        mutableStateOf(
            if (isEditMode && productToEdit != null) productToEdit.status ?: "draft" else "draft"
        )
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
    val hasSizeAndColor =
        variantList.all { !it.option1.isNullOrBlank() && !it.option2.isNullOrBlank() }

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
    val isLoading =
        currentState is AddRestProductState.Loading || currentState is UpdateRestProductState.Loading

    LaunchedEffect(title, description, productTypeOptions, vendorOptions, variantList) {
        val allVariantsValid =
            variantList.all { it.price.toDoubleOrNull()?.let { p -> p > 0 } ?: false }
        isFormValid = title.isNotBlank() && description.isNotBlank() &&
                productTypeOptions.isNotEmpty() && vendorOptions.isNotEmpty() && allVariantsValid
    }


    LaunchedEffect(addProductState) {
        when (addProductState) {
            is AddRestProductState.Success -> {
                // Reset form
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
                title = ""
                description = ""
                productTypeOptions = listOf(" ")
                vendorOptions = listOf(" ")
                selectedStatus = "draft"
                selectedImages = emptyList()

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
                    IconButton(onClick = { if (!isEditMode) navController?.popBackStack() else onBackPressed?.invoke() }) {
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
                        selectedOption = (if (selectedProductType?.isNotEmpty() == true) selectedProductType else customProductType).toString(),
                        onOptionSelected = { selectedProductType = it },
                        customInput = customProductType.toString(),
                        onCustomInputChange = { customProductType = it }
                    )

                    // Vendor Dropdown
                    DropdownWithCustomInput(
                        label = "Vendor",
                        options = vendorOptions,
                        selectedOption = (if (selectedVendor?.isNotEmpty() == true) selectedVendor else customVendor).toString(),
                        onOptionSelected = { selectedVendor = it },
                        customInput = customVendor.toString(),
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
                    colors = CheckboxDefaults.colors(
                        checkedColor = Teal
                    ),
                    onCheckedChange = {
                        hasVariants = it
                        variantList = if (!it) {
                            listOf(RestProductVariantInput(option1 = "Default", price = ""))
                        } else {
                            listOf(
                                RestProductVariantInput(option1 = "Size", price = ""),
                                RestProductVariantInput(option2 = "Color", price = "")
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

                        FormField(
                            value = singleProductPrice,
                            onValueChange = { singleProductPrice = it },
                            label = "Price",
                            placeholder = "e.g., 99.99",
                            keyboardType = KeyboardType.Number,
                            isError = singleProductPrice.isBlank()
                        )

                        Spacer(modifier = Modifier.height(8.dp))


                        FormField(
                            value = singleProductSku,
                            onValueChange = { singleProductSku = it },
                            label = "SKU (Optional)",
                            placeholder = "e.g., SKU001",

                            isError = false
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        FormField(
                            value = singleProductQuantity.toString(),
                            onValueChange = { singleProductQuantity = it.toIntOrNull() ?: 0 },
                            label = "Inventory Quantity",
                            placeholder = "e.g., 50",
                            keyboardType = KeyboardType.Number,
                            isError = false
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
                                variantList =
                                    variantList.toMutableList().also { it[index] = updated }
                            },
                            onAddVariant = {
                                variantList = variantList + RestProductVariantInput(
                                    option1 = " ",
                                    price = " "
                                )

                            },
                            onRemoveVariant = { index ->
                                variantList =
                                    variantList.toMutableList().also { it.removeAt(index) }
                            }
                        )
                    }
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
                colors = CardDefaults.cardColors(LightGray80)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ImagePicker(
                        selectedImages = selectedImages,
                        onImagesSelected = {
                            selectedImages = it
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
                    price = singleProductPrice,
                    sku = singleProductSku,
                    inventoryQuantity = singleProductQuantity
                )
                variantList = listOf(singleVariant)
            }

            // Submit Button
            Button(
                onClick = {
                    if (isEditMode && productToEdit != null) {
                        val updateInput = RestProductUpdateInput(
                            title = title,
                            productType = if (selectedProductType!!.isNotEmpty()) selectedProductType else customProductType,
                            vendor = if (selectedVendor!!.isNotEmpty()) selectedVendor else customVendor,

                            options = productToEdit.options?.map { option ->
                                RestProductOptionInput(
                                    name = option.name,
                                    values = option.values,
                                    position = option.position
                                )
                            } ?: emptyList(),
                            status = selectedStatus,
                            variants = productToEdit.variants.mapIndexedNotNull { index, existing ->
                                variantList.getOrNull(index)?.let {
                                    RestProductVariantUpdateInput(

                                        id = existing.id,
                                        price = it.price,
                                        sku = it.sku,
                                        title = it.title ?: "Default Title",

                                        inventoryManagement = it.inventoryManagement,
                                        inventoryPolicy = it.inventoryPolicy,
                                        fulfillmentService = it.fulfillmentService,
                                        weight = it.weight,
                                        weightUnit = it.weightUnit,
                                        barcode = it.barcode,
                                        taxable = it.taxable,
                                        requiresShipping = it.requiresShipping,
                                        inventoryQuantity = it.inventoryQuantity ?: 0,
                                    )
                                }
                            }
                        )

                        viewModel.updateProduct(
                            productToEdit.id,
                            updateInput,
                            context,
                            selectedImages
                        )

                    } else {

                        val productInput = RestProductInput(

                            title = title,
                            descriptionHtml = description,
                            productType = selectedProductType,
                            vendor = selectedVendor,
                            status = selectedStatus,
                            variants = variantList,
                            options = options,
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


private fun String.capitalize(): String {
    return if (this.isNotEmpty()) {
        this[0].uppercase() + this.substring(1).lowercase()
    } else {
        this
    }
}