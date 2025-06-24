package com.example.m_commerce_admin.features.products.presentation.viewModel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.m_commerce_admin.features.products.domain.entity.DomainProductInput
import com.example.m_commerce_admin.features.products.domain.entity.Product
import com.example.m_commerce_admin.features.products.domain.usecase.AddProductWithImagesParams
import com.example.m_commerce_admin.features.products.domain.usecase.AddProductWithImagesUseCase
import com.example.m_commerce_admin.features.products.domain.usecase.GetAllProductsUseCase
import com.example.m_commerce_admin.features.products.domain.usecase.GetProductsParams
import com.example.m_commerce_admin.features.products.presentation.states.AddProductState
import com.example.m_commerce_admin.features.products.presentation.states.GetProductState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.m_commerce_admin.features.products.domain.usecase.DeleteProductUseCase

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val getAllProductsUseCase: GetAllProductsUseCase,
    private val addProductWithImagesUseCase: AddProductWithImagesUseCase,
    private val deleteProductUseCase: DeleteProductUseCase,
) : ViewModel() {

    private val _productsState = MutableStateFlow<GetProductState>(GetProductState.Loading)
    val productsState: StateFlow<GetProductState> = _productsState

    private val currentList = mutableListOf<Product>()
    private var cursor: String? = null
    private var hasNextPage = true
    private var isPaginating = false

    private val _uiAddProductState = MutableStateFlow<AddProductState>(AddProductState.Idle)
    val uiAddProductState: StateFlow<AddProductState> = _uiAddProductState

    private val _deleteProductState = MutableStateFlow<Result<Unit>?>(null)
    val deleteProductState: StateFlow<Result<Unit>?> = _deleteProductState

    fun loadMoreProducts() {
        if (isPaginating || !hasNextPage) {
            return
        }

        isPaginating = true

        viewModelScope.launch {
            try {
                getAllProductsUseCase(
                    GetProductsParams(
                        first = 10,
                        after = cursor
                    )
                ).collect { state ->
                    when (state) {
                        is GetProductState.Success -> {
                            val newCursor = state.endCursor
                            if (newCursor == cursor) {
                                hasNextPage = false
                                isPaginating = false
                                return@collect
                            }
                            val newProducts = state.data.filterNot { newProduct ->
                                currentList.any { it.id == newProduct.id }
                            }

                            if (newProducts.isEmpty()) {
                                hasNextPage = false
                                isPaginating = false
                                return@collect
                            }

                            currentList.addAll(newProducts)
                            cursor = newCursor
                            hasNextPage = state.hasNext

                            _productsState.value =
                                GetProductState.Success(currentList.toList(), hasNextPage, cursor)
                        }

                        is GetProductState.Error -> {
                            _productsState.value = state
                        }

                        is GetProductState.Loading -> {
                            if (currentList.isEmpty()) {
                                _productsState.value = state
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                _productsState.value = GetProductState.Error(e.message ?: "Unknown error")
            } finally {
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

    fun addProductWithImages(product: DomainProductInput, imageUris: List<Uri>, context: Context) {
        viewModelScope.launch {
            _uiAddProductState.value = AddProductState.Loading

            try {
                val result = addProductWithImagesUseCase(
                    AddProductWithImagesParams(
                        product = product,
                        imageUris = imageUris,
                        context = context
                    )
                )

                if (result.isSuccess) {
                    _uiAddProductState.value = AddProductState.Success

                    launch {
                        delay(1000)
                        refreshProducts()
                    }
                } else {
                    val errorMessage = result.exceptionOrNull()?.message ?: "Unknown error occurred"
                    _uiAddProductState.value = AddProductState.Error(errorMessage)
                }
            } catch (e: Exception) {
                _uiAddProductState.value =
                    AddProductState.Error(e.message ?: "An unexpected error occurred")
            }
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            _deleteProductState.value = null
            val result = deleteProductUseCase(productId)
            _deleteProductState.value = result
            if (result.isSuccess) {
                val removed = currentList.removeAll { it.id == productId }
                _productsState.value = GetProductState.Success(currentList.toList(), hasNextPage, cursor)
            }
        }
    }

}
