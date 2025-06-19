package com.example.m_commerce_admin.core.shared.components.top_app_bar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.m_commerce_admin.features.login.presentation.LoginViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTAB(
    title: String,
    loginViewModel: LoginViewModel = hiltViewModel(),
    toLogin: () -> Unit
) {
    TopAppBar(
        title = { Text(title) },
        actions = {
            var expanded by remember { mutableStateOf(false) }

            IconButton(onClick = { expanded = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "Menu")
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Logout") },
                    onClick = {
                        expanded = false
                        loginViewModel.logout()
                        toLogin()
                    }
                )
            }
        }
    )
}
