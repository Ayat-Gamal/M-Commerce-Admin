package com.example.m_commerce_admin.features.products.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.m_commerce_admin.config.theme.Black
import com.example.m_commerce_admin.config.theme.Gray
import com.example.m_commerce_admin.config.theme.LightGray80
import com.example.m_commerce_admin.config.theme.Teal

@Composable
fun DropdownWithCustomInput(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    customInput: String,
    onCustomInputChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, fontWeight = FontWeight.Bold)
        Box(
            modifier = Modifier.background(color = LightGray80),
        ) {
            OutlinedTextField(
                shape = RoundedCornerShape(16.dp),

                value = selectedOption,
                onValueChange = { onOptionSelected(it) },
                label = { Text("Select $label") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Teal,
                    unfocusedBorderColor = Gray,
                    focusedLabelColor = Teal,
                    errorBorderColor = Teal
                )
            )
            DropdownMenu(
                modifier = Modifier.background(LightGray80),
                expanded = expanded, onDismissRequest = { expanded = false }) {

                options.forEach { option ->
                    DropdownMenuItem(
                        modifier = Modifier.background(color = LightGray80),
                        colors = MenuDefaults.itemColors(textColor = Black),
                        text = { Text(option) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }

        if (selectedOption == "Other") {
            OutlinedTextField(
                value = customInput,
                onValueChange = onCustomInputChange,
                label = { Text("Enter custom $label") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),

                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Teal,
                    unfocusedBorderColor = Gray,
                    focusedLabelColor = Teal,
                    errorBorderColor = Teal
                )
            )
        }
    }
}
