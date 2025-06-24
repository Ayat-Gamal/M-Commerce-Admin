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
            { FAB(onClick = { navController.navigate(AppRoutes.ProductForm)}, screen = "Product") }

        }

        isRouteSelected(AppRoutes.InventoryScreen, currentDestination) -> {
            { FAB(onClick = { /*TODO(Add Inventory screen)*/ }, screen = "Inventory") }
        }

        isRouteSelected(AppRoutes.CouponScreen, currentDestination) -> {
            { FAB(onClick = {  /*TODO(Add Coupon screen)*/ }, screen = "Coupon") }
        }

        else -> null
    }
}
