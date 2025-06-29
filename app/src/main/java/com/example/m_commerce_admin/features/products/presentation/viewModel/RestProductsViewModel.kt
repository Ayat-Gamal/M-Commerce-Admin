package com.example.m_commerce_admin.features.products.presentation.viewModel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.m_commerce_admin.BuildConfig
import com.example.m_commerce_admin.features.inventory.domain.usecase.AdjustInventoryUseCase
import com.example.m_commerce_admin.features.inventory.domain.usecase.params.AdjustInventoryLevelParam
import com.example.m_commerce_admin.features.products.domain.entity.rest.RestProduct
import com.example.m_commerce_admin.features.products.domain.entity.rest.RestProductImageInput
import com.example.m_commerce_admin.features.products.domain.entity.rest.RestProductInput
import com.example.m_commerce_admin.features.products.domain.entity.rest.RestProductOptionInput
import com.example.m_commerce_admin.features.products.domain.entity.rest.RestProductUpdateInput
import com.example.m_commerce_admin.features.products.domain.entity.rest.RestProductVariantInput
import com.example.m_commerce_admin.features.products.domain.usecase.PublishProductUseCase
import com.example.m_commerce_admin.features.products.domain.usecase.params.AddRestProductWithImagesParams
import com.example.m_commerce_admin.features.products.domain.usecase.params.GetAllRestProductsParams
import com.example.m_commerce_admin.features.products.domain.usecase.params.UpdateRestProductParams
import com.example.m_commerce_admin.features.products.domain.usecase.rest.AddRestProductWithImagesUseCase
import com.example.m_commerce_admin.features.products.domain.usecase.rest.CreateRestProductUseCase
import com.example.m_commerce_admin.features.products.domain.usecase.rest.DeleteRestProductUseCase
 import com.example.m_commerce_admin.features.products.domain.usecase.rest.GetAllRestProductsUseCase
import com.example.m_commerce_admin.features.products.domain.usecase.rest.SetInventoryLevelUseCase
 import com.example.m_commerce_admin.features.products.domain.usecase.rest.UpdateRestProductUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RestProductsViewModel @Inject constructor(
    private val getAllRestProductsUseCase: GetAllRestProductsUseCase,
    private val createRestProductUseCase: CreateRestProductUseCase,
    private val addRestProductWithImagesUseCase: AddRestProductWithImagesUseCase,
    private val deleteRestProductUseCase: DeleteRestProductUseCase,
    private val updateRestProductUseCase: UpdateRestProductUseCase,
    private val publishProductUseCase: PublishProductUseCase,
    private val setInventoryLevelUseCase: SetInventoryLevelUseCase,
    private val adjustInventoryUseCase: AdjustInventoryUseCase
) : ViewModel() {
    fun testAddProductWithVariants(context: Context) {
        val options = listOf(
//            RestProductOptionInput(
//                name = "Size",
//                position = 1,
//                values = listOf("Small", "Medium", "Large")
//            ),
            RestProductOptionInput(
                name = "Color",
                position = 1,
                values = listOf("Red", "Blue")
            )
        )

        val variants = listOf(
            RestProductVariantInput(
                //   option1 = "Small",
                option1 = "Red",
                price = "99.99",
                sku = "S-RED",
                inventoryQuantity = 10
            ),
            RestProductVariantInput(
                // option1 = "Medium",
                option1 = "Red",
                price = "109.99",
                sku = "M-RED",
                inventoryQuantity = 5
            ),
            RestProductVariantInput(
                // option1 = "Large",
                option1 = "Blue",
                price = "119.99",
                sku = "L-BLUE",
                inventoryQuantity = 7
            )
        )
        val images = listOf(
            RestProductImageInput(
                src = "https://cdn.shopify.com/s/files/1/0755/0271/5129/collections/smart_collections_2.jpg?v=1749927992", // use any valid public URL
                alt = "Test Image"
            )
        )
        val productInput = RestProductInput(
            title = "Saad Color Only",
            descriptionHtml = "This is a test product with predefined Size and Color variants.",
            productType = "Clothing",
            vendor = "TestVendor",
            status = "active",
            tags = "test,static",
            options = options,
            variants = variants,
            images = images

        )


        // Use empty list for images for now
        addProduct(productInput, emptyList(), context)
    }

    private val _productsState = MutableStateFlow<RestProductsState>(RestProductsState.Idle)
    val productsState: StateFlow<RestProductsState> = _productsState.asStateFlow()

    private val _addProductState = MutableStateFlow<AddRestProductState>(AddRestProductState.Idle)
    val addProductState: StateFlow<AddRestProductState> = _addProductState.asStateFlow()

    private val _deleteProductState =
        MutableStateFlow<DeleteRestProductState>(DeleteRestProductState.Idle)
    val deleteProductState: StateFlow<DeleteRestProductState> = _deleteProductState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedStatus = MutableStateFlow<String?>(null)
    val selectedStatus: StateFlow<String?> = _selectedStatus.asStateFlow()

    private val _allProducts = MutableStateFlow<List<RestProduct>>(emptyList())
    private val _filteredProducts = MutableStateFlow<List<RestProduct>>(emptyList())

    private var currentPageInfo: String? = null
    private var isLoadingMore = false

    private val _updateProductState =
        MutableStateFlow<UpdateRestProductState>(UpdateRestProductState.Idle)
    val updateProductState: StateFlow<UpdateRestProductState> = _updateProductState.asStateFlow()

    init {
        getAllProducts()
    }

    fun getAllProducts(limit: Int = 50, pageInfo: String? = null, status: String? = null) {
        if (pageInfo == null) {
            _productsState.value = RestProductsState.Loading
            currentPageInfo = null
        } else {
            isLoadingMore = true
        }

        viewModelScope.launch {
            getAllRestProductsUseCase(
                GetAllRestProductsParams(
                    limit,
                    pageInfo,
                    status
                )
            ).collectLatest { result ->
                result.fold(
                    onSuccess = { products ->
                        if (pageInfo == null) {

                            _allProducts.value = products
                            currentPageInfo = null
                        } else {

                            _allProducts.value = _allProducts.value + products
                        }

                        applyFilters()
                        isLoadingMore = false
                        _productsState.value = RestProductsState.Success(_filteredProducts.value)
                    },
                    onFailure = { error ->
                        isLoadingMore = false
                        _productsState.value =
                            RestProductsState.Error(error.message ?: "Unknown error")
                    }
                )
            }
        }
    }

    fun addProduct(
        productInput: RestProductInput,
        imageUris: List<Uri> = emptyList(),
        context: Context? = null
    ) {
        _addProductState.value = AddRestProductState.Loading
        Log.d("DEBUG", "Submitting product: $productInput")

        viewModelScope.launch {
            val result = if (imageUris.isNotEmpty() && context != null) {
                addRestProductWithImagesUseCase(
                    AddRestProductWithImagesParams(
                        product = productInput,
                        imageUris = imageUris,
                        context = context
                    )
                )

            } else {
                createRestProductUseCase(productInput)
            }

            result.fold(
                onSuccess = { product ->
                    try {
                        Log.d("DEBUG", "Product successfully created: $product")

                        _addProductState.value = AddRestProductState.Success(product)
                        getAllProducts(status = _selectedStatus.value)
                        publishProductUseCase.invoke(product.id)

                        val firstVariant = product.variants.firstOrNull()
                        Log.d("DEBUG", "First variant: $firstVariant")

                        val inventoryItemId = firstVariant?.inventoryItemId
                        val quantity = firstVariant?.quantity

                        if (inventoryItemId != null && quantity != null) {
                            Log.d("DEBUG", "Adjusting inventory: $inventoryItemId -> $quantity")
                            delay(2000) // 1 second delay before adjusting inventory
                            val inventoryItemId = product.variants.first().inventoryItemId
                            val quantity = product.variants.first().quantity


                            // New: Set inventory
                            val setResult = setInventoryLevelUseCase(
                                inventoryItemId,
                                BuildConfig.locationID.toLong(),
                                quantity
                            )

                        } else {
                            Log.w("DEBUG", "Variant or inventoryItemId is missing")
                        }

                    } catch (e: Exception) {
                        Log.e("DEBUG", "Exception in onSuccess block: ${e.message}", e)
                    }
                },
                onFailure = { error ->
                    _addProductState.value = AddRestProductState.Error(
                        error.message ?: "An unexpected error occurred"
                    )
                }
            )
        }
    }


    fun resetAddProductState() {
        _addProductState.value = AddRestProductState.Idle
    }

    fun deleteProduct(productId: Long) {
        _deleteProductState.value = DeleteRestProductState.Loading
        viewModelScope.launch {
            val result = deleteRestProductUseCase(productId)
            result.fold(
                onSuccess = {
                    _deleteProductState.value = DeleteRestProductState.Success(
                        _allProducts.value.find { it.id == productId } ?: RestProduct(
                            id = productId,
                            title = "",
                            descriptionHtml = null,
                            productType = null,
                            vendor = null,
                            status = null,
                            createdAt = null,
                            updatedAt = null,
                            publishedAt = null,
                            templateSuffix = null,
                            handle = null,
                            tags = null,
                            variants = emptyList(),
                            images = null,
                            options = null,

                            )
                    )
                    // Refresh the products list
                    getAllProducts(status = _selectedStatus.value)
                },
                onFailure = { error ->
                    _deleteProductState.value =
                        DeleteRestProductState.Error(error.message ?: "Failed to delete product")
                }
            )
        }
    }

    fun resetDeleteProductState() {
        _deleteProductState.value = DeleteRestProductState.Idle
    }

    fun loadMoreProducts() {
        if (!isLoadingMore && currentPageInfo != null) {
            getAllProducts(pageInfo = currentPageInfo, status = _selectedStatus.value)
        }
    }

    fun refreshProducts() {
        getAllProducts(status = _selectedStatus.value)
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        applyFilters()
    }

    fun updateStatusFilter(status: String?) {
        _selectedStatus.value = status
        getAllProducts(status = status)
    }

    private fun applyFilters() {
        val query = _searchQuery.value.lowercase()
        val status = _selectedStatus.value

        val filtered = _allProducts.value.filter { product ->
            val matchesSearch = query.isEmpty() ||
                    product.title.lowercase().contains(query) ||
                    product.vendor?.lowercase()?.contains(query) == true ||
                    product.productType?.lowercase()?.contains(query) == true

            val matchesStatus = status == null ||
                    product.status?.lowercase() == status.lowercase()

            matchesSearch && matchesStatus
        }

        _filteredProducts.value = filtered
        _productsState.value = RestProductsState.Success(filtered)
    }

    fun clearFilters() {
        _searchQuery.value = ""
        _selectedStatus.value = null
        applyFilters()
    }

    fun updateProduct(productId: Long, updateInput: RestProductUpdateInput) {
        _updateProductState.value = UpdateRestProductState.Loading
        viewModelScope.launch {
            val result = updateRestProductUseCase(
                UpdateRestProductParams(productId, updateInput)
            )
            result.fold(
                onSuccess = { product ->
                    _updateProductState.value = UpdateRestProductState.Success(product)
                    getAllProducts(status = _selectedStatus.value)

                    val inventoryItemId = product.variants.first().inventoryItemId
                    delay(2000)

                    adjustInventoryUseCase.invoke(
                        AdjustInventoryLevelParam(
                            inventoryItemId,
                            product.variants.first().quantity
                        )
                    )
                    Log.d(
                        "DEBUG",
                        "addProduct: ${inventoryItemId}${product.variants.first().quantity}"
                    )
                },
                onFailure = { error ->
                    _updateProductState.value =
                        UpdateRestProductState.Error(error.message ?: "Failed to update product")
                }
            )
        }
    }

    fun resetUpdateProductState() {
        _updateProductState.value = UpdateRestProductState.Idle
    }
}

sealed class RestProductsState {
    object Idle : RestProductsState()
    object Loading : RestProductsState()
    data class Success(val products: List<RestProduct>) : RestProductsState()
    data class Error(val message: String) : RestProductsState()
}

sealed class AddRestProductState {
    object Idle : AddRestProductState()
    object Loading : AddRestProductState()
    data class Success(val product: RestProduct) : AddRestProductState()
    data class Error(val message: String) : AddRestProductState()
}

sealed class DeleteRestProductState {
    object Idle : DeleteRestProductState()
    object Loading : DeleteRestProductState()
    data class Success(val product: RestProduct) : DeleteRestProductState()
    data class Error(val message: String) : DeleteRestProductState()
}

sealed class UpdateRestProductState {
    object Idle : UpdateRestProductState()
    object Loading : UpdateRestProductState()
    data class Success(val product: RestProduct) : UpdateRestProductState()
    data class Error(val message: String) : UpdateRestProductState()
} 