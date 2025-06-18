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
    object LoginScreen : AppRoutes()
}