package com.example.m_commerce_admin.core.shared.components.bottom_nav_bar

import com.example.m_commerce_admin.R
import com.example.m_commerce_admin.config.constant.NavRoutes
import com.example.m_commerce_admin.config.routes.AppRoutes
import kotlinx.serialization.Serializable

@Serializable
sealed class NavigationItem(
    val title: String,
    val route: AppRoutes,
    val icon: Int,

)
{
    object Home : NavigationItem("home", AppRoutes.HomeScreen, R.raw.home)
    object Product : NavigationItem("product", AppRoutes.ProductScreen , R.raw.products)
    object Inventory : NavigationItem("inventory", AppRoutes.InventoryScreen , R.raw.inventory)
    object Coupons : NavigationItem("coupons", AppRoutes.CouponScreen , R.raw.coupon)
}