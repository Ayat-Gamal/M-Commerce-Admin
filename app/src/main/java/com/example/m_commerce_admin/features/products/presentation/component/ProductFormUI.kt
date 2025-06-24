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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Red
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
import com.example.m_commerce_admin.features.products.domain.entity.DomainProductInput
import com.example.m_commerce_admin.features.products.domain.entity.ProductImage
import com.example.m_commerce_admin.features.products.domain.entity.ProductStatus
import com.example.m_commerce_admin.features.products.presentation.states.AddProductState
import com.example.m_commerce_admin.features.products.presentation.viewModel.ProductsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
    var price by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var inStock by remember { mutableStateOf(true) }
    var selectedStatus by remember { mutableStateOf(ProductStatus.ACTIVE) }
    var selectedImages by remember { mutableStateOf(listOf<Uri>()) }
    var isFormValid by remember { mutableStateOf(false) }

    val state by viewModel.uiAddProductState.collectAsState()
    val scrollState = rememberScrollState()
    val snackbarHostState  = remember { SnackbarHostState() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(title, description, productType, vendor, price, category) {
        val isPriceValid = price.toDoubleOrNull()?.let { it > 0 } ?: false
        isFormValid = title.isNotBlank() && description.isNotBlank() && productType.isNotBlank() &&
                vendor.isNotBlank() && price.isNotBlank() && isPriceValid && category.isNotBlank()
    }

    LaunchedEffect(state) {
        if (state is AddProductState.Success) {
            title = ""
            description = ""
            productType = ""
            vendor = ""
            price = ""
            category = ""
            inStock = true
            selectedStatus = ProductStatus.ACTIVE
            selectedImages = emptyList()
            delay(2000)
            scope.launch {
                snackbarHostState.showSnackbar("Product added Successfully!")
            }
         //   navController?.popBackStack()
        } else if (state is AddProductState.Error) {
            scope.launch {
                snackbarHostState.showSnackbar("Error: ${(state as AddProductState.Error).message}")
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
            FormField(value = title, onValueChange = { title = it }, label = "Product Title", placeholder = "Enter product name", isError = title.isBlank() && !isFormValid)
            FormField(value = description, onValueChange = { description = it }, label = "Description", placeholder = "Describe product", isError = description.isBlank() && !isFormValid, singleLine = false)
            FormField(value = price, onValueChange = {
                if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) price = it
            }, label = "Price", placeholder = "0.00", isError = price.isBlank() || price.toDoubleOrNull()?.let { it <= 0 } ?: true, keyboardType = KeyboardType.Decimal)
            FormField(value = category, onValueChange = { category = it }, label = "Category", placeholder = "e.g., Electronics", isError = category.isBlank() && !isFormValid)
            FormField(value = productType, onValueChange = { productType = it }, label = "Product Type", placeholder = "e.g., T-Shirt", isError = productType.isBlank() && !isFormValid)
            FormField(value = vendor, onValueChange = { vendor = it }, label = "Vendor", placeholder = "Brand name", isError = vendor.isBlank() && !isFormValid)

            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(Color(0xFFF8F9FA))) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("In Stock", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Switch(checked = inStock, onCheckedChange = { inStock = it })
                }
            }

            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(Color(0xFFF8F9FA))) {
                Column(Modifier.padding(16.dp)) {
                    Text("Product Status", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ProductStatus.entries.forEach { status ->
                            FilterChip(
                                onClick = { selectedStatus = status },
                                label = { Text(status.name) },
                                selected = selectedStatus == status,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(Color(0xFFF8F9FA))) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Product Images", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    ImagePicker(selectedImages = selectedImages, onImagesSelected = { selectedImages = it })
                }
            }

            Button(
                onClick = {
                    val input = DomainProductInput(
                        title = title,
                        descriptionHtml = description,
                        productType = productType,
                        vendor = vendor,
                        status = selectedStatus,
                        price = price,
                        category = category,
                        inStock = inStock,
                        images = selectedImages.map { uri ->
                            ProductImage(uri.toString(), uri.lastPathSegment ?: "image.jpg", "image/jpeg")
                        }
                    )
                    viewModel.addProductWithImages(input, selectedImages, context)
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = isFormValid && state !is AddProductState.Loading
            ) {
                if (state is AddProductState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text("Add Product", fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
        label = { Text("$label" ,style = TextStyle(color = DarkestGray )) },
        placeholder = { Text(placeholder,style = TextStyle(color = DarkestGray )) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = singleLine,
        isError = isError,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        supportingText = {
            if (isError) Text("$label is required", color = Teal)
        },

        shape = RoundedCornerShape(16.dp),
     )
}
