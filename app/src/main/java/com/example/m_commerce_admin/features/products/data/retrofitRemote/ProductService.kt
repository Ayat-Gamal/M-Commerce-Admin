package com.example.m_commerce_admin.features.products.data.retrofitRemote

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET


interface ShopifyProductApi{
@GET("admin/api/2024-04/products.json")
suspend fun getAllProducts(): ProductsResponse
}

data class ProductsResponse(
    @SerializedName("products") val products: List<ProductDto>
)


data class ProductDto(
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String,
    @SerializedName("body_html") val descriptionHtml: String?,
    @SerializedName("product_type") val productType: String?,
    @SerializedName("vendor") val vendor: String?,
    @SerializedName("variants") val variants: List<VariantDto>
)

data class VariantDto(
    @SerializedName("id") val id: Long,
    @SerializedName("sku") val sku: String?,
    @SerializedName("price") val price: String,
    @SerializedName("inventory_item_id") val inventoryItemId: Long,
    @SerializedName("inventory_quantity") val quantity: Int
)
