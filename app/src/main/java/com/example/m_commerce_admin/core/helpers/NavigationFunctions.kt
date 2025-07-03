package com.example.m_commerce_admin.core.helpers

import androidx.navigation.NavHostController
import com.example.m_commerce_admin.config.routes.AppRoutes

fun NavHostController.navigateAndClear(route: AppRoutes) {
    navigate(route) {
        popUpTo(0) { inclusive = true }
    }
}