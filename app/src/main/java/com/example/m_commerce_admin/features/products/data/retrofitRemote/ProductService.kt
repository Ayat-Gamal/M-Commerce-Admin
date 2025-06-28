package com.example.m_commerce_admin.features.products.data.retrofitRemote

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import retrofit2.http.*

interface ShopifyProductApi {
    @GET("products.json")
    suspend fun getAllProducts(
        @Query("limit") limit: Int = 50,
        @Query("page_info") pageInfo: String? = null,
        @Query("status") status: String? = null
    ): ProductsResponse

    @GET("products/{productId}.json")
    suspend fun getProductById(@Path("productId") productId: Long): ProductResponse

    @POST("products.json")
    suspend fun createProduct(@Body request: CreateProductRequest): ProductResponse

    @PUT("products/{productId}.json")
    suspend fun updateProduct(
        @Path("productId") productId: Long,
        @Body request: UpdateProductRequest
    ): ProductResponse

    @DELETE("products/{productId}.json")
    suspend fun deleteProduct(@Path("productId") productId: Long): DeleteProductResponse

    // Asset API for image uploads
    @GET("themes/{themeId}/assets.json")
    suspend fun getAssets(@Path("themeId") themeId: Long): AssetsResponse

    @PUT("themes/{themeId}/assets.json")
    suspend fun uploadAsset(
        @Path("themeId") themeId: Long,
        @Body request: AssetUploadRequest
    ): AssetResponse

    // Inventory management endpoints
    @GET("inventory_levels.json")
    suspend fun getInventoryLevels(
        @Query("inventory_item_ids") inventoryItemIds: String
    ): InventoryLevelsResponse

    @POST("inventory_levels/set.json")
    suspend fun setInventoryLevel(@Body request: SetInventoryLevelRequest): SetInventoryLevelResponse

    @POST("inventory_levels/adjust.json")
    suspend fun adjustInventoryLevel(@Body request: AdjustInventoryLevelRequest): AdjustInventoryLevelResponse
}

// Response Models
data class ProductsResponse(
    @SerializedName("products") val products: List<ProductDto>
)

data class ProductResponse(
    @SerializedName("product") val product: ProductDto
)

data class DeleteProductResponse(
    @SerializedName("message") val message: String? = null
)

data class InventoryLevelsResponse(
    @SerializedName("inventory_levels") val inventoryLevels: List<InventoryLevelDto>
)

data class SetInventoryLevelResponse(
    @SerializedName("inventory_level") val inventoryLevel: InventoryLevelDto
)

data class AdjustInventoryLevelResponse(
    @SerializedName("inventory_level") val inventoryLevel: InventoryLevelDto
)

// Request Models
data class CreateProductRequest(
    @SerializedName("product") val product: ProductCreateDto
)

data class UpdateProductRequest(
    @SerializedName("product") val product: ProductUpdateDto
)

data class SetInventoryLevelRequest(
    @SerializedName("location_id") val locationId: Long,
    @SerializedName("inventory_item_id") val inventoryItemId: Long,
    @SerializedName("available") val available: Int
)

data class AdjustInventoryLevelRequest(
    @SerializedName("location_id") val locationId: Long,
    @SerializedName("inventory_item_id") val inventoryItemId: Long,
    @SerializedName("available_adjustment") val availableAdjustment: Int
)

// Data Transfer Objects
data class ProductDto(
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String,
    @SerializedName("body_html") val descriptionHtml: String?,
    @SerializedName("product_type") val productType: String?,
    @SerializedName("vendor") val vendor: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?,
    @SerializedName("published_at") val publishedAt: String?,
    @SerializedName("template_suffix") val templateSuffix: String?,
    @SerializedName("handle") val handle: String?,
    @SerializedName("tags") val tags: String?,
    @SerializedName("variants") val variants: List<VariantDto>,
    @SerializedName("images") val images: List<ImageDto>?,
    @SerializedName("options") val options: List<OptionDto>?
)

data class VariantDto(
    @SerializedName("id") val id: Long,
    @SerializedName("sku") val sku: String?,
    @SerializedName("price") val price: String,
    @SerializedName("inventory_item_id") val inventoryItemId: Long,
    @SerializedName("inventory_quantity") val quantity: Int,
    @SerializedName("title") val title: String?,
    @SerializedName("weight") val weight: Double?,
    @SerializedName("weight_unit") val weightUnit: String?,
    @SerializedName("barcode") val barcode: String?,
    @SerializedName("inventory_management") val inventoryManagement: String?,
    @SerializedName("inventory_policy") val inventoryPolicy: String?,
    @SerializedName("fulfillment_service") val fulfillmentService: String?,
    @SerializedName("taxable") val taxable: Boolean?,
    @SerializedName("requires_shipping") val requiresShipping: Boolean?
)

data class ImageDto(
    @SerializedName("id") val id: Long,
    @SerializedName("src") val src: String,
    @SerializedName("alt") val alt: String?,
    @SerializedName("width") val width: Int?,
    @SerializedName("height") val height: Int?,
    @SerializedName("position") val position: Int?
)

data class OptionDto(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("position") val position: Int,
    @SerializedName("values") val values: List<String>?
)

data class InventoryLevelDto(
    @SerializedName("inventory_item_id") val inventoryItemId: Long,
    @SerializedName("location_id") val locationId: Long,
    @SerializedName("available") val available: Int,
    @SerializedName("updated_at") val updatedAt: String?
)

// Create/Update DTOs
data class ProductCreateDto(
    @SerializedName("title") val title: String,
    @SerializedName("body_html") val descriptionHtml: String? = null,
    @SerializedName("product_type") val productType: String? = null,
    @SerializedName("vendor") val vendor: String? = null,
    @SerializedName("status") val status: String? = "draft",
    @SerializedName("tags") val tags: String? = null,
    @SerializedName("variants") val variants: List<VariantCreateDto>? = null,
    @SerializedName("images") val images: List<ImageCreateDto>? = null,
    @SerializedName("options") val options: List<OptionCreateDto>? = null
)

data class ProductUpdateDto(
    @SerializedName("title") val title: String? = null,
    @SerializedName("body_html") val descriptionHtml: String? = null,
    @SerializedName("product_type") val productType: String? = null,
    @SerializedName("vendor") val vendor: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("tags") val tags: String? = null,
    @SerializedName("variants") val variants: List<VariantUpdateDto>? = null,
    @SerializedName("images") val images: List<ImageCreateDto>? = null,
    @SerializedName("options") val options: List<OptionCreateDto>? = null
)

data class VariantCreateDto(
    @SerializedName("option1") val option1: String? = null,
    @SerializedName("option2") val option2: String? = null,

    @SerializedName("price") val price: String,
    @SerializedName("sku") val sku: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("weight") val weight: Double? = null,
    @SerializedName("weight_unit") val weightUnit: String? = null,
    @SerializedName("barcode") val barcode: String? = null,
    @SerializedName("inventory_management") val inventoryManagement: String? = "shopify",
    @SerializedName("inventory_policy") val inventoryPolicy: String? = "deny",
    @SerializedName("fulfillment_service") val fulfillmentService: String? = "manual",
    @SerializedName("taxable") val taxable: Boolean? = true,
    @SerializedName("requires_shipping") val requiresShipping: Boolean? = true,
    @SerializedName("inventory_quantity") val inventoryQuantity: Int? = 0

)

data class VariantUpdateDto(
    @SerializedName("id") val id: Long,
    @SerializedName("price") val price: String? = null,
    @SerializedName("sku") val sku: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("weight") val weight: Double? = null,
    @SerializedName("weight_unit") val weightUnit: String? = null,
    @SerializedName("barcode") val barcode: String? = null,
    @SerializedName("inventory_management") val inventoryManagement: String? = null,
    @SerializedName("inventory_policy") val inventoryPolicy: String? = null,
    @SerializedName("fulfillment_service") val fulfillmentService: String? = null,
    @SerializedName("taxable") val taxable: Boolean? = null,
    @SerializedName("requires_shipping") val requiresShipping: Boolean? = null
)

data class ImageCreateDto(
    @SerializedName("src") val src: String,
    @SerializedName("alt") val alt: String? = null,
    @SerializedName("position") val position: Int? = null
)

data class OptionCreateDto(
    @SerializedName("name") val name: String,
    @SerializedName("position") val position: Int,
    @SerializedName("values") val values: List<String>? = null
)

data class AssetsResponse(
    @SerializedName("assets") val assets: List<AssetDto>
)

data class AssetResponse(
    @SerializedName("asset") val asset: AssetDto
)

data class AssetUploadRequest(
    @SerializedName("asset") val asset: AssetCreateDto
)

// Asset DTOs
data class AssetDto(
    @SerializedName("key") val key: String,
    @SerializedName("public_url") val publicUrl: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?,
    @SerializedName("content_type") val contentType: String?,
    @SerializedName("size") val size: Long?
)

data class AssetCreateDto(
    @SerializedName("key") val key: String,
    @SerializedName("attachment") val attachment: String? = null,
    @SerializedName("src") val src: String? = null,
    @SerializedName("content_type") val contentType: String? = null
)
