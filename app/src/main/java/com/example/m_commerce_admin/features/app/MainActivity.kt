package com.example.m_commerce_admin.features.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.m_commerce_admin.config.routes.AppRoutes
import com.example.m_commerce_admin.config.routes.NavSetup
import com.example.m_commerce_admin.config.theme.MCommerceAdminTheme
import com.example.m_commerce_admin.core.helpers.isRouteSelected
import com.example.m_commerce_admin.core.shared.components.bottom_nav_bar.BottomNavigationBar
import com.example.m_commerce_admin.features.app.component.FAB

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
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination


    Scaffold(

        modifier = Modifier.safeContentPadding(),
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        },

        floatingActionButton = {
            when {
                isRouteSelected(AppRoutes.ProductScreen, currentRoute) -> FAB(
                    onClick = {},
                    screen = "Product"
                )

                isRouteSelected(AppRoutes.InventoryScreen, currentRoute) -> FAB(
                    onClick = {},
                    screen = "Inventory"
                )

                isRouteSelected(AppRoutes.CouponScreen, currentRoute) -> FAB(
                    onClick = {},
                    screen = "Coupon "
                )

                else -> {}
            }
        },
        //  containerColor = Color.White,
        bottomBar = { BottomNavigationBar(navController) }

    ) { innerPadding ->
        val bottomPadding = Modifier.padding(
            start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
            top = innerPadding.calculateTopPadding(),
            end = innerPadding.calculateEndPadding(LayoutDirection.Ltr),
            bottom = 0.dp
        )

        NavSetup(navController, snackBarHostState, modifier = bottomPadding)

    }
}



