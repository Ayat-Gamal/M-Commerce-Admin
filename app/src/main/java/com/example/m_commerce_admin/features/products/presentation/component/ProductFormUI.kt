package com.example.m_commerce_admin.features.products.presentation.component


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.apollographql.apollo.api.Optional
import com.example.m_commerce_admin.config.theme.Black
import com.example.m_commerce_admin.config.theme.DarkestGray
import com.example.m_commerce_admin.config.theme.LightGreen
import com.example.m_commerce_admin.config.theme.LightTeal
import com.example.m_commerce_admin.config.theme.Teal
import com.example.m_commerce_admin.config.theme.White
import com.example.m_commerce_admin.config.theme.lightRed
import com.example.m_commerce_admin.core.shared.components.SmallButton
import com.example.m_commerce_admin.features.products.presentation.states.AddProductState
import com.example.m_commerce_admin.features.products.presentation.viewModel.ProductsViewModel
import com.example.m_commerce_admin.type.ProductInput
import com.example.m_commerce_admin.type.ProductStatus

@Composable
fun ProductFormUI(viewModel: ProductsViewModel = hiltViewModel(), onCancelClick: () -> Unit) {
    val scrollState = rememberScrollState()

    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var productType by rememberSaveable { mutableStateOf("") }
    var vendor by rememberSaveable { mutableStateOf("") }
    var price by rememberSaveable { mutableStateOf("") }
    var quantity by rememberSaveable { mutableStateOf("") }
    var imageUrl by rememberSaveable { mutableStateOf("") }
    var status by rememberSaveable { mutableStateOf(ProductStatus.ACTIVE) }

    val state by viewModel.uiAddProductState.collectAsState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 80.dp)
        ) {
            Text("Add New Product", style = MaterialTheme.typography.titleMedium, color = Teal)

            Spacer(modifier = Modifier.height(12.dp))

            CustomTextField(label = "Title", value = title) { title = it }
            CustomTextField(label = "Description", value = description) { description = it }
            CustomTextField(label = "Product Type", value = productType) { productType = it }
            CustomTextField(label = "Vendor", value = vendor) { vendor = it }
            CustomTextField(
                label = "Price",
                value = price,
                keyboardType = KeyboardType.Number
            ) { price = it }
            CustomTextField(
                label = "Quantity",
                value = quantity,
                keyboardType = KeyboardType.Number
            ) { quantity = it }
            CustomTextField(label = "Image URL", value = imageUrl) { imageUrl = it }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Absolute.Left,
                verticalAlignment = Alignment.CenterVertically

            ) {
                Text(
                    "Status:",
                    color = DarkestGray,
                    style = TextStyle(fontWeight = FontWeight.Bold)
                )
                Spacer(Modifier.width(4.dp))
                DropdownMenuWithStatus(selected = status) { status = it }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,

                ) {
                SmallButton(
                    modifier = Modifier.weight(1f),
                    label = "Cancel"
                ) { onCancelClick() }

                Spacer(modifier = Modifier.width(8.dp))

                Button(

                    onClick = {
                        val input = ProductInput(
                            title = Optional.presentIfNotNull(title),
                            descriptionHtml = Optional.presentIfNotNull(description),
                            productType = Optional.presentIfNotNull(productType),
                            vendor = Optional.presentIfNotNull(vendor),
                            status = Optional.presentIfNotNull(status)
                        )
                        viewModel.addProduct(input)
                    },
                    modifier = Modifier
                        .height(40.dp)
                        .weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Teal)
                ) {
                    Text("Add Product", color = White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (state) {
                is AddProductState.Loading -> CircularProgressIndicator(color = Teal)
                is AddProductState.Success -> Text(
                    "Product added successfully!",
                    color = LightGreen
                )

                is AddProductState.Error -> Text(
                    "Error: ${(state as AddProductState.Error).message}",
                    color = lightRed
                )

                else -> {}
            }
        }
    }
}

@Composable
fun CustomTextField(
    label: String,
    value: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = DarkestGray) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Teal,
            unfocusedBorderColor = LightTeal,
            cursorColor = Teal
        )
    )
}

@Composable
fun DropdownMenuWithStatus(selected: ProductStatus, onStatusSelected: (ProductStatus) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box (modifier = Modifier.fillMaxHeight()){
        Button(

            onClick = { expanded = true },
            colors = ButtonDefaults.buttonColors(containerColor = LightTeal)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = selected.name,
                    fontSize = 12.sp,
                    color = DarkestGray
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Default.ArrowDropDown,contentDescription = "Dropdown Arrow", tint = Black  )
            }
        }
        DropdownMenu(
            modifier = Modifier.background(color = LightTeal),
            expanded = expanded, onDismissRequest = { expanded = false }) {
            ProductStatus.entries.forEach { status ->
                DropdownMenuItem(

                    text = { Text(status.name) }, onClick = {
                    onStatusSelected(status)
                    expanded = false
                })
            }
        }
    }
}