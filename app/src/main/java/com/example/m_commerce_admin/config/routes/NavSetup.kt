package com.example.m_commerce_admin.config.routes

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.m_commerce_admin.features.coupons.CouponScreenUI
import com.example.m_commerce_admin.features.home.presentation.HomeScreenUI
import com.example.m_commerce_admin.features.inventory.InventoryScreenUI
import com.example.m_commerce_admin.features.products.ProductScreenUI


@Composable
fun NavSetup(
    navController: NavHostController,
    snackBarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {

    NavHost(
        navController = navController,
        startDestination = AppRoutes.HomeScreen,
        modifier = modifier.padding( 16.dp )
    ) {
        composable<AppRoutes.HomeScreen> {
            HomeScreenUI()
        }

        composable<AppRoutes.ProductScreen> {
            ProductScreenUI()
        }

        composable<AppRoutes.InventoryScreen> {
            InventoryScreenUI()
        }
        composable<AppRoutes.CouponScreen> {
            CouponScreenUI()
        }
    }
}