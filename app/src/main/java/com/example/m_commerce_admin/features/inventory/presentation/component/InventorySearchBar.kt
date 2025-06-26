package com.example.m_commerce_admin.features.inventory.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.m_commerce_admin.config.theme.DarkestGray
import com.example.m_commerce_admin.config.theme.Teal
import com.example.m_commerce_admin.features.inventory.presentation.viewModel.InventoryFilter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventorySearchBar(
    searchQuery: String,
    selectedFilter: InventoryFilter,
    onSearchQueryChange: (String) -> Unit,
    onFilterChange: (InventoryFilter) -> Unit,
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showFilterDropdown by remember { mutableStateOf(false) }
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text("Search by product name, SKU, or ID...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Teal
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear search",
                            tint = Teal
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Filter Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Filter Dropdown
            TextButton(
                onClick = { showFilterDropdown = true },
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Filter: ${selectedFilter.displayName}",
                    color = Teal,
                    fontWeight = FontWeight.Medium
                )
            }
            
            DropdownMenu(
                expanded = showFilterDropdown,
                onDismissRequest = { showFilterDropdown = false }
            ) {
                InventoryFilter.values().forEach { filter ->
                    DropdownMenuItem(
                        text = { Text(filter.displayName) },
                        onClick = {
                            onFilterChange(filter)
                            showFilterDropdown = false
                        }
                    )
                }
            }
            
            // Clear Filters Button
            if (searchQuery.isNotEmpty() || selectedFilter != InventoryFilter.ALL) {
                TextButton(
                    onClick = onClearFilters
                ) {
                    Text(
                        text = "Clear",
                        color = DarkestGray,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
} 