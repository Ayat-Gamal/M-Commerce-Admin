package com.example.m_commerce_admin.features.products.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.m_commerce_admin.features.products.domain.entity.Product
 import com.example.m_commerce_admin.features.products.domain.usecase.AddProductUseCase
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
    private  val addProductUseCase: AddProductUseCase
) : ViewModel() {

    private val _productsState = MutableStateFlow<GetProductState>(GetProductState.Loading)
    val productsState: StateFlow<GetProductState> = _productsState

    private val currentList = mutableListOf<Product>()
    private var cursor: String? = null
    private var hasNextPage = true
    private var isPaginating = false


    fun loadInitialProducts() {
        cursor = null
        currentList.clear()
        _productsState.value = GetProductState.Loading
        loadMoreProducts()
    }

    fun loadMoreProducts() {
        if (isPaginating || !hasNextPage) return

        isPaginating = true

        viewModelScope.launch {
            getAllProductsUseCase(GetProductsParams(first = 10, after = cursor)).collect { state ->
                when (state) {
                    is GetProductState.Success -> {
                        currentList.addAll(state.data)
                        cursor = state.endCursor
                        hasNextPage = state.hasNext
                        _productsState.value = GetProductState.Success(currentList, hasNextPage, cursor)
                    }
                    is GetProductState.Error -> {
                        _productsState.value = state
                    }
                    else -> {}
                }
                isPaginating = false
            }
        }
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

}
