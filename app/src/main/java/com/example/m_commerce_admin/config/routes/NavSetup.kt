package com.example.m_commerce_admin.config.routes

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.m_commerce_admin.core.helpers.navigateAndClear
import com.example.m_commerce_admin.features.coupons.CouponScreenUI
import com.example.m_commerce_admin.features.home.presentation.HomeScreenUI
import com.example.m_commerce_admin.features.home.presentation.orders.OrdersScreenUI
import com.example.m_commerce_admin.features.inventory.InventoryScreenUI
import com.example.m_commerce_admin.features.login.presentation.LoginScreenUI
import com.example.m_commerce_admin.features.products.ProductScreenUI
import com.example.m_commerce_admin.features.products.presentation.component.ProductFormUI
import com.example.m_commerce_admin.features.splash.SplashScreenUI


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavSetup(
    navController: NavHostController,
    snackBarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    showBottomNavbar: MutableState<Boolean>
) {
    val startingScreen = AppRoutes.SplashScreen

    NavHost(
        navController = navController,
        startDestination = startingScreen,
        modifier = modifier.padding(0.dp)
    ) {
        composable<AppRoutes.HomeScreen> {
            showBottomNavbar.value = true

            HomeScreenUI(
                navController = navController,

                )
        }

        composable<AppRoutes.ProductScreen> {
            showBottomNavbar.value = true

            ProductScreenUI()
        }

        composable<AppRoutes.InventoryScreen> {
            showBottomNavbar.value = true

            InventoryScreenUI()
        }
        composable<AppRoutes.CouponScreen> {
            showBottomNavbar.value = true

            CouponScreenUI()
        }
        composable<AppRoutes.SplashScreen> {
            showBottomNavbar.value = false

            SplashScreenUI { route ->
                navController.navigateAndClear(route)

            }
        }
        composable<AppRoutes.LoginScreen> {
            showBottomNavbar.value = false
            LoginScreenUI(snackBarHostState = snackBarHostState) {
                navController.navigateAndClear(AppRoutes.HomeScreen)

            }
        }
        composable<AppRoutes.OrdersScreen> {
            OrdersScreenUI() {
                navController.popBackStack()
            }
        }
        composable<AppRoutes.ProductForm> {
            showBottomNavbar.value = false

            ProductFormUI(navController = navController)

        }

    }
}