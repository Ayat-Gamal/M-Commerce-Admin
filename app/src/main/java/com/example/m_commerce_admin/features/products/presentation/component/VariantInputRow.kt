package com.example.m_commerce_admin.features.products.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.m_commerce_admin.features.products.domain.entity.rest.RestProductVariantInput

@Composable
fun VariantInputRow(
    variant: RestProductVariantInput,
    onVariantChange: (RestProductVariantInput) -> Unit,
    onDeleteClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Size (optional)
        FormField(
            value = variant.option1 ?: " ",
            onValueChange = { onVariantChange(variant.copy(option1 = it)) },
            label = "Size (e.g. M, L, XL)",
             singleLine = true,
            isError = false
        )

// Color (optional)
        FormField(
            value = variant.option2 ?: "",
            onValueChange = { onVariantChange(variant.copy(option2 = it)) },
            label = "Color (e.g. Red, Blue)",
            singleLine = true,
            isError = false
        )


        Spacer(modifier = Modifier.height(8.dp))

        // Price
        FormField(
            value = variant.price,
            onValueChange = { onVariantChange(variant.copy(price = it)) },
            label = "Price",
            placeholder = "e.g., 49.99",
            keyboardType = KeyboardType.Number,
            isError = variant.price.isBlank()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // SKU
        FormField(
            value = variant.sku ?: "",
            onValueChange = { onVariantChange(variant.copy(sku = it)) },
            label = "SKU",
            placeholder = "e.g., SKU123 or leave blank",
            isError = false
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Inventory Quantity
        FormField(
            value = variant.inventoryQuantity?.toString() ?: "",
            onValueChange = {
                val quantity = it.toIntOrNull()
                onVariantChange(variant.copy(inventoryQuantity = quantity ?: 0))
            },
            label = "Inventory Quantity",
            placeholder = "e.g., 10",
            keyboardType = KeyboardType.Number,
            isError = false
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Delete button
        IconButton(
            onClick = onDeleteClick,
            modifier = Modifier.align(Alignment.End)
        ) {
            Icon(Icons.Default.Delete, contentDescription = "Delete Variant")
        }
    }
}