package com.example.m_commerce_admin.config.routes

import kotlinx.serialization.Serializable

@Serializable
sealed class AppRoutes {

    @Serializable
    object HomeScreen : AppRoutes()

    @Serializable
    object ProductScreen : AppRoutes()

    @Serializable
    object InventoryScreen : AppRoutes()

    @Serializable
    object CouponScreen : AppRoutes()

    @Serializable
    data class UpdateCouponForm(val id:String?) : AppRoutes()
    @Serializable
   object AddCouponForm : AppRoutes()

    @Serializable
    object LoginScreen : AppRoutes()

    @Serializable
    object SplashScreen : AppRoutes()


    @Serializable
    object OrdersScreen : AppRoutes()

    @Serializable
    object ProductForm : AppRoutes()

    @Serializable
    object RestProductForm : AppRoutes()



}