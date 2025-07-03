package com.example.m_commerce_admin.features.products.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.m_commerce_admin.config.theme.Teal
import com.example.m_commerce_admin.features.products.domain.entity.rest.RestProductVariantInput

@Composable
fun VariantListSection(
    variants: List<RestProductVariantInput>,
    onUpdateVariant: (Int, RestProductVariantInput) -> Unit,
    onAddVariant: () -> Unit,
    onRemoveVariant: (Int) -> Unit,

) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "Variants", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        variants.forEachIndexed { index, variant ->
            VariantInputRow(

                variant = variant,
                onVariantChange = { onUpdateVariant(index, it) },
                onDeleteClick = { onRemoveVariant(index) }
            )
            Spacer(Modifier.height(8.dp))
        }

        Button(
            onClick = onAddVariant,
            modifier = Modifier.align(Alignment.End),
            colors = ButtonDefaults.buttonColors(containerColor = Teal)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add")
            Spacer(modifier = Modifier.width(4.dp))
            Text("Add Variant")
        }
    }
}
