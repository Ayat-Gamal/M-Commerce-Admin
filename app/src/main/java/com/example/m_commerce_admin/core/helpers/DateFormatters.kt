package com.example.m_commerce_admin.core.helpers

fun formatCreatedAt(createdAt: Int): String {
    return try {
        val formatter = java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.getDefault())
        val date = formatter.parse(createdAt.toString())
        val outputFormat = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
        outputFormat.format(date!!)
    } catch (e: Exception) {
        throw NumberFormatException()
    }
}
