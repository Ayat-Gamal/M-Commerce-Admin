package com.example.m_commerce_admin.features.products.presentation.viewModel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.m_commerce_admin.features.products.domain.entity.rest.RestProduct
import com.example.m_commerce_admin.features.products.domain.entity.rest.RestProductInput
import com.example.m_commerce_admin.features.products.domain.entity.rest.RestProductUpdateInput
import com.example.m_commerce_admin.features.products.domain.usecase.PublishProductUseCase
import com.example.m_commerce_admin.features.products.domain.usecase.params.AddRestProductWithImagesParams
import com.example.m_commerce_admin.features.products.domain.usecase.params.GetAllRestProductsParams
import com.example.m_commerce_admin.features.products.domain.usecase.params.UpdateRestProductParams
import com.example.m_commerce_admin.features.products.domain.usecase.rest.AddRestProductWithImagesUseCase
import com.example.m_commerce_admin.features.products.domain.usecase.rest.CreateRestProductUseCase
import com.example.m_commerce_admin.features.products.domain.usecase.rest.DeleteRestProductUseCase
import com.example.m_commerce_admin.features.products.domain.usecase.rest.GetAllRestProductsUseCase
import com.example.m_commerce_admin.features.products.domain.usecase.rest.UpdateRestProductUseCase
import com.example.m_commerce_admin.features.products.domain.usecase.params.UploadExistProductImagesParams
import com.example.m_commerce_admin.features.products.domain.usecase.rest.UploadImagesToExistingProductUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val uploadImagesToExistingProductUseCase: UploadImagesToExistingProductUseCase
) : ViewModel() {

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

                            _allProducts.value += products
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
                    _addProductState.value = AddRestProductState.Success(product)
                    getAllProducts(status = _selectedStatus.value)
                    publishProductUseCase.invoke(product.id)
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

    fun updateProduct(
        productId: Long,
        updateInput: RestProductUpdateInput,
        context: Context? = null,
        imagesUri: List<Uri> = emptyList()
    ) {
        _updateProductState.value = UpdateRestProductState.Loading

        viewModelScope.launch {
            val result = updateRestProductUseCase(UpdateRestProductParams(productId, updateInput))

            if (result.isSuccess && context != null && imagesUri.isNotEmpty()) {
                updateProductWithImages(
                    productId = productId,
                    updateInput = updateInput,
                    imageUris = imagesUri,
                    context = context
                )
            }

            result.fold(
                onSuccess = { product ->
                    _updateProductState.value = UpdateRestProductState.Success(product)
                    getAllProducts(status = _selectedStatus.value)
                },
                onFailure = { error ->
                    _updateProductState.value = UpdateRestProductState.Error(
                        error.message ?: "Failed to update product"
                    )
                }
            )
        }
    }

    fun updateProductWithImages(
        productId: Long,
        updateInput: RestProductUpdateInput,
        imageUris: List<Uri>,
        context: Context
    ) {
        viewModelScope.launch {
            val result = updateRestProductUseCase(UpdateRestProductParams(productId, updateInput))

            result.fold(
                onSuccess = { updatedProduct ->
                    if (imageUris.isNotEmpty()) {
                        val imageResult = uploadImagesToExistingProductUseCase(
                            UploadExistProductImagesParams(
                                productId = updatedProduct.id,
                                imageUris = imageUris,
                                context = context
                            )
                        )

                        imageResult.onFailure {
                            // Optional: set error state
                        }
                    }
                },
                onFailure = { error ->
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