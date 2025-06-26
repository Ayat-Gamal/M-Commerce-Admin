package com.example.m_commerce_admin.config.constant

object ShopifyConfig {
    // Default theme ID for asset uploads
    // You can change this to your actual theme ID
    const val DEFAULT_THEME_ID = 1L
    
    // API configuration
    const val API_VERSION = "2024-01"
    const val MAX_PRODUCTS_PER_PAGE = 50
    
    // Image upload configuration
    const val MAX_IMAGE_SIZE_MB = 20
    val SUPPORTED_IMAGE_TYPES = listOf("image/jpeg", "image/png", "image/webp", "image/gif")
} 