package com.example.m_commerce_admin.features.products.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.m_commerce_admin.features.products.domain.entity.Product
import com.example.m_commerce_admin.features.products.domain.usecase.GetAllProductsUseCase
import com.example.m_commerce_admin.features.products.domain.usecase.GetProductsParams
import com.example.m_commerce_admin.features.products.presentation.ProductState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val getAllProductsUseCase: GetAllProductsUseCase
) : ViewModel() {

    private val _productsState = MutableStateFlow<ProductState>(ProductState.Loading)
    val productsState: StateFlow<ProductState> = _productsState

    private val currentList = mutableListOf<Product>()
    private var cursor: String? = null
    private var hasNextPage = true
    private var isPaginating = false


    fun loadInitialProducts() {
        cursor = null
        currentList.clear()
        _productsState.value = ProductState.Loading
        loadMoreProducts()
    }

    fun loadMoreProducts() {
        if (isPaginating || !hasNextPage) return

        isPaginating = true

        viewModelScope.launch {
            getAllProductsUseCase(GetProductsParams(first = 10, after = cursor)).collect { state ->
                when (state) {
                    is ProductState.Success -> {
                        currentList.addAll(state.data)
                        cursor = state.endCursor
                        hasNextPage = state.hasNext
                        _productsState.value = ProductState.Success(currentList, hasNextPage, cursor)
                    }
                    is ProductState.Error -> {
                        _productsState.value = state
                    }
                    else -> {}
                }
                isPaginating = false
            }
        }
    }


}
