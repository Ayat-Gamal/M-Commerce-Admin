package com.example.m_commerce_admin.features.products.data.mapper

import android.util.Log
import com.example.m_commerce_admin.GetProductsQuery
import com.example.m_commerce_admin.features.products.domain.entity.Product

fun GetProductsQuery.Node.toDomain(): Product {
     val firstVariant = this.variants.edges.firstOrNull()?.node

    val imageUrls: List<String> = this.images.edges.mapNotNull { edge ->
        edge.node.url.toString()
    }
    
    val product = Product(
        id = this.id,
        title = this.title,
        status = this.status.rawValue,
        createdAt = this.createdAt.toString(),
        totalInventory = this.totalInventory ?: 0,
        featuredImage = this.featuredImage?.url.toString(),
        price = firstVariant?.price.toString() ?: "0.00",
        sku = firstVariant?.sku ?: "",
        variantId = firstVariant?.id ?: "",
        variantTitle = firstVariant?.title ?: "Default Title",
        inventoryQuantity = firstVariant?.inventoryQuantity ?: 0,
        images = imageUrls
    )
    
    Log.d("ProductMapper", "📦 Mapped product: ${product.title}")
    Log.d("ProductMapper", "📊 Status: ${product.status}")
    Log.d("ProductMapper", "💰 Price: ${product.price}")
    Log.d("ProductMapper", "📦 Stock: ${product.inventoryQuantity}")
    Log.d("ProductMapper", "🏷️ SKU: ${product.sku}")
    Log.d("ProductMapper", "🖼️ Featured Image: ${product.featuredImage}")
    Log.d("ProductMapper", "🖼️ Total Images: ${product.images.size}")
    Log.d("ProductMapper", "🖼️ Image URLs: ${product.images}")
    
    return product
}
