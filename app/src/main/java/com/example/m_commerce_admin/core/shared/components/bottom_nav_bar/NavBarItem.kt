package com.example.m_commerce_admin.core.shared.components.bottom_nav_bar

import com.example.m_commerce_admin.R
import com.example.m_commerce_admin.config.constant.NavRoutes
import com.example.m_commerce_admin.config.constant.Screens
import com.example.m_commerce_admin.config.routes.AppRoutes
import kotlinx.serialization.Serializable

@Serializable
sealed class NavigationItem(
    val title: String,
    val route: AppRoutes,
    val icon: Int,

)
{
    object Home : NavigationItem(Screens.HOME_SCREEN, AppRoutes.HomeScreen, R.raw.home)
    object Product : NavigationItem(Screens.PRODUCT_SCREEN, AppRoutes.ProductScreen , R.raw.products)
    object Inventory : NavigationItem(Screens.INVENTORY_SCREEN, AppRoutes.InventoryScreen , R.raw.inventory)
    object Coupons : NavigationItem(Screens.COUPONS_SCREEN, AppRoutes.CouponScreen , R.raw.coupon)
}