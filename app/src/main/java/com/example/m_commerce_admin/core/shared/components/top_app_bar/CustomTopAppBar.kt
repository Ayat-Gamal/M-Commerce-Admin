package com.example.m_commerce_admin.core.shared.components.top_app_bar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.m_commerce_admin.features.login.presentation.viewModel.LoginViewModel


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

            IconButton(onClick = {
                loginViewModel.logout()
                toLogin()
            }) {
                Icon(Icons.Default.Logout, contentDescription = "Menu")
            }


        }
    )
}
