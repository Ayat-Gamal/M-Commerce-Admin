package com.example.m_commerce_admin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.m_commerce_admin.config.routes.NavSetup
import com.example.m_commerce_admin.config.theme.MCommerceAdminTheme
import com.example.m_commerce_admin.core.shared.components.bottom_nav_bar.BottomNavigationBar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
           MCommerceAdminTheme {
                Main()
           }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Main() {
    val navController = rememberNavController()
    val snackBarHostState = remember { SnackbarHostState() }
    Scaffold(

        modifier = Modifier.safeContentPadding(),
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        },
containerColor = Color.White,
        bottomBar = { BottomNavigationBar(navController) }

    ) { innerPadding ->
        NavSetup(navController, snackBarHostState, modifier = Modifier.padding(innerPadding))

    }
}