package com.example.m_commerce_admin.features.app.component

import androidx.compose.runtime.Composable
import androidx.navigation.NavController

import androidx.navigation.NavDestination
import com.example.m_commerce_admin.config.routes.AppRoutes
import com.example.m_commerce_admin.core.helpers.isRouteSelected

fun getFABForRouteWithAction(
    currentDestination: NavDestination?,
    navController: NavController
): @Composable (() -> Unit)? {
    return when {
        isRouteSelected(AppRoutes.ProductScreen, currentDestination) -> {
            {
                FAB(onClick = { navController.navigate(AppRoutes.RestProductForm) }, screen = "Product")
            }

        }

        isRouteSelected(AppRoutes.CouponScreen, currentDestination) -> {
            {
                FAB(onClick = { navController.navigate(AppRoutes.AddCouponForm) }, screen = "Coupon")
            }
        }

        else -> null
    }
}
