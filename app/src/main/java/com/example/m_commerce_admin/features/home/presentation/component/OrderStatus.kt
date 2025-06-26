package com.example.m_commerce_admin.features.home.presentation.component

import androidx.compose.ui.graphics.Color
import com.example.m_commerce_admin.config.theme.LightGreen
import com.example.m_commerce_admin.config.theme.Teal
import com.example.m_commerce_admin.config.theme.lightRed

enum class OrderStatus(
    val displayName: String,
    val color: Color
) {
    PENDING("Pending", Teal),
    PROCESSING("Processing", Teal),
    SHIPPED("Shipped", LightGreen),
    DELIVERED("Delivered", LightGreen),
    CANCELLED("Cancelled", lightRed),
    REFUNDED("Refunded", lightRed),
    UNKNOWN("Unknown", Teal);

    companion object {
        fun fromString(status: String?): OrderStatus {
            return when (status?.uppercase()) {
                "PENDING" -> PENDING
                "PROCESSING" -> PROCESSING
                "SHIPPED" -> SHIPPED
                "DELIVERED" -> DELIVERED
                "CANCELLED" -> CANCELLED
                "REFUNDED" -> REFUNDED
                else -> UNKNOWN
            }
        }
    }
} 