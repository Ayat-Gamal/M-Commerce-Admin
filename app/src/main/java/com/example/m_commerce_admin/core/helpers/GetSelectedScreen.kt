package com.example.m_commerce_admin.core.helpers

import androidx.navigation.NavDestination
import com.example.m_commerce_admin.config.routes.AppRoutes

fun isRouteSelected(route: AppRoutes, currentDestination: NavDestination?): Boolean {
    val currentRoute = currentDestination?.route ?: return false
    return when (route) {
        is AppRoutes.ProductScreen -> currentRoute.contains("ProductScreen")
        is AppRoutes.HomeScreen -> currentRoute.contains("HomeScreen")
        is AppRoutes.InventoryScreen -> currentRoute.contains("InventoryScreen")
        is AppRoutes.CouponScreen -> currentRoute.contains("CouponScreen")
        else -> {
            false
        }
    }
}
