package com.example.m_commerce_admin.features.products.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.m_commerce_admin.features.products.data.mapper.toGraphQL
import com.example.m_commerce_admin.features.products.data.remote.ProductRemoteDataSource
import com.example.m_commerce_admin.features.products.domain.entity.DomainProductInput
import com.example.m_commerce_admin.features.products.domain.entity.Product
import com.example.m_commerce_admin.features.products.domain.usecase.AddProductUseCase
import com.example.m_commerce_admin.features.products.domain.usecase.AddProductWithImagesUseCase
import com.example.m_commerce_admin.features.products.domain.usecase.GetAllProductsUseCase
import com.example.m_commerce_admin.features.products.domain.usecase.GetProductsParams
import com.example.m_commerce_admin.features.products.presentation.states.AddProductState
import com.example.m_commerce_admin.features.products.presentation.states.GetProductState
import com.example.m_commerce_admin.type.ProductInput
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val getAllProductsUseCase: GetAllProductsUseCase,
    private val addProductUseCase: AddProductUseCase,
    private val addProductWithImagesUseCase: AddProductWithImagesUseCase,
 ) : ViewModel() {

    private val _productsState = MutableStateFlow<GetProductState>(GetProductState.Loading)
    val productsState: StateFlow<GetProductState> = _productsState

    private val currentList = mutableListOf<Product>()
    private var cursor: String? = null
    private var hasNextPage = true
    private var isPaginating = false

    // Test method to check basic connectivity
    fun testConnection() {
        viewModelScope.launch {
            try {
                Log.d("ProductsViewModel", "Testing connection...")
             } catch (e: Exception) {
                Log.e("ProductsViewModel", "Test connection failed", e)
            }
        }
    }

    fun loadMoreProducts() {
        if (isPaginating || !hasNextPage) return

        isPaginating = true
        viewModelScope.launch {
            try {
                val params = GetProductsParams(
                    first = 10,
                    after = cursor
                )

                getAllProductsUseCase(params).collect { state ->
                    when (state) {
                        is GetProductState.Loading -> {
                            if (cursor == null) {
                                _productsState.value = GetProductState.Loading
                            }
                        }
                        is GetProductState.Success -> {
                            if (cursor == null) {
                                currentList.clear()
                            }
                            currentList.addAll(state.products)
                            cursor = state.endCursor
                            hasNextPage = state.hasNextPage
                            _productsState.value = GetProductState.Success(
                                products = currentList.toList(),
                                hasNextPage = hasNextPage,
                                endCursor = cursor
                            )
                        }
                        is GetProductState.Error -> {
                            _productsState.value = GetProductState.Error(state.message)
                        }
                    }
                    isPaginating = false
                }
            } catch (e: Exception) {
                Log.e("ProductsViewModel", "Error loading products", e)
                _productsState.value = GetProductState.Error(e.message ?: "Unknown error")
                isPaginating = false
            }
        }
    }

    fun refreshProducts() {
        cursor = null
        hasNextPage = true
        currentList.clear()
        loadMoreProducts()
    }

    private val _uiAddProductState = MutableStateFlow<AddProductState>(AddProductState.Idle)
    val uiAddProductState: StateFlow<AddProductState> = _uiAddProductState

    fun addProduct(product: ProductInput) {
        viewModelScope.launch {
            _uiAddProductState.value = AddProductState.Loading
            val result = addProductUseCase(product)
            _uiAddProductState.value = if (result.isSuccess) {
                AddProductState.Success
            } else {
                AddProductState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    fun addProductWithImages(product: DomainProductInput, imageUris: List<String>) {
        viewModelScope.launch {
            _uiAddProductState.value = AddProductState.Loading

            val params = AddProductWithImagesUseCase.AddProductWithImagesParams(
                product = product,
                imageUris = imageUris
            )
            
            val result = addProductWithImagesUseCase(params)
            _uiAddProductState.value = if (result.isSuccess) {
                AddProductState.Success
            } else {
                AddProductState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

}
