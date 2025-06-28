package com.example.m_commerce_admin.features.products.presentation.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
        OutlinedTextField(
            value = variant.option1 ?: "",
            onValueChange = { onVariantChange(variant.copy(option1 = it)) },
            label = { Text("Size (e.g. M, L, XL)") },
            placeholder = { Text("Leave empty if not applicable") },
            modifier = Modifier.fillMaxWidth()
        )

// Color (optional)
        OutlinedTextField(
            value = variant.option2 ?: "",
            onValueChange = { onVariantChange(variant.copy(option2 = it)) },
            label = { Text("Color (e.g. Red, Blue)") },
            placeholder = { Text("Leave empty if not applicable") },
            modifier = Modifier.fillMaxWidth()
        )


        Spacer(modifier = Modifier.height(8.dp))

        // Price
        OutlinedTextField(
            value = variant.price,
            onValueChange = { onVariantChange(variant.copy(price = it)) },
            label = { Text("Price") },
            placeholder = { Text("e.g., 49.99") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // SKU
        OutlinedTextField(
            value = variant.sku ?: "",
            onValueChange = { onVariantChange(variant.copy(sku = it)) },
            label = { Text("SKU") },
            placeholder = { Text("e.g., SKU123 or leave blank") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Inventory Quantity
        OutlinedTextField(
            value = variant.inventoryQuantity?.toString() ?: "",
            onValueChange = {
                val quantity = it.toIntOrNull()
                onVariantChange(variant.copy(inventoryQuantity = quantity ?: 0))
            },
            label = { Text("Inventory Quantity") },
            placeholder = { Text("e.g., 10") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
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