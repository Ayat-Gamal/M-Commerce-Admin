package com.example.m_commerce_admin.features.app.component

import androidx.compose.runtime.Composable
import androidx.navigation.NavDestination
import com.example.m_commerce_admin.config.routes.AppRoutes
import com.example.m_commerce_admin.core.helpers.isRouteSelected
import com.example.m_commerce_admin.core.shared.components.top_app_bar.CustomTAB


@Composable
fun getTopAppBarForRoute(
    currentDestination: NavDestination?,
    toLogin: () -> Unit
): @Composable (() -> Unit)? {
    return when {
        isRouteSelected(AppRoutes.HomeScreen, currentDestination) -> {
            { CustomTAB(title = "Home", toLogin = toLogin) }
        }

        isRouteSelected(AppRoutes.ProductScreen, currentDestination) -> {
            { CustomTAB(title = "Product", toLogin = toLogin) }
        }

        isRouteSelected(AppRoutes.InventoryScreen, currentDestination) -> {
            { CustomTAB(title = "Inventory", toLogin = toLogin) }
        }

        isRouteSelected(AppRoutes.CouponScreen, currentDestination) -> {
            { CustomTAB(title = "Coupon", toLogin = toLogin) }
        }

        else -> {
            null
        }
    }
}