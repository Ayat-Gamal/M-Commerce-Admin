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
import androidx.navigation.toRoute
import com.example.m_commerce_admin.core.helpers.navigateAndClear
import com.example.m_commerce_admin.features.coupons.presentation.CouponScreenUI
import com.example.m_commerce_admin.features.coupons.presentation.component.AddCouponFormUI
import com.example.m_commerce_admin.features.coupons.presentation.component.AddForm
import com.example.m_commerce_admin.features.home.presentation.HomeScreenUI
import com.example.m_commerce_admin.features.home.presentation.orders.OrdersScreenUI
import com.example.m_commerce_admin.features.inventory.presentation.InventoryScreenUI
import com.example.m_commerce_admin.features.login.presentation.LoginScreenUI
import com.example.m_commerce_admin.features.products.ProductScreenUI
import com.example.m_commerce_admin.features.products.presentation.component.RestProductFormUI

import com.example.m_commerce_admin.features.splash.SplashScreenUI


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavSetup(
    navController: NavHostController,
    snackBarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    showBottomNavbar: MutableState<Boolean>,
    isConnected: Boolean,

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
                isConnected = isConnected
            )
        }

        composable<AppRoutes.ProductScreen> {
            showBottomNavbar.value = true

            ProductScreenUI(isConnected = isConnected)
        }

        composable<AppRoutes.InventoryScreen> {
            showBottomNavbar.value = true

            InventoryScreenUI(isConnected = isConnected)
        }
        composable<AppRoutes.CouponScreen> {
            showBottomNavbar.value = true

            CouponScreenUI(navController = navController, isConnected = isConnected)
        }
        composable<AppRoutes.UpdateCouponForm> {
            showBottomNavbar.value = false
            val data = it.toRoute<AppRoutes.UpdateCouponForm>()
            AddCouponFormUI(navController = navController, couponId = data.id)
        }
        composable<AppRoutes.AddCouponForm> {
            showBottomNavbar.value = false
            AddForm(navController = navController)
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

        composable<AppRoutes.RestProductForm> {
            showBottomNavbar.value = false
            RestProductFormUI(
                navController = navController, snackBarHostState = snackBarHostState
            )
        }

    }
}