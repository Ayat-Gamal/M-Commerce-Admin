package com.example.m_commerce_admin.features.products.presentation.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
            .padding(8.dp)
            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Variant", fontWeight = FontWeight.Bold)
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Variant")
            }
        }

        OutlinedTextField(
            value = variant.option1 ?: "",
            onValueChange = { onVariantChange(variant.copy(option1 = it)) },
            label = { Text("Option (e.g. Size)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = variant.price,
            onValueChange = { onVariantChange(variant.copy(price = it)) },
            label = { Text("Price") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = variant.inventoryQuantity?.toString() ?: "",
            onValueChange = {
                val quantity = it.toIntOrNull()
                onVariantChange(variant.copy(inventoryQuantity = quantity ?: 0))
            },
            label = { Text("Inventory Quantity") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
