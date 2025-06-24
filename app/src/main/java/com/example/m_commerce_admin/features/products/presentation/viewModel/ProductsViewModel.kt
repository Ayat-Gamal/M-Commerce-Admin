package com.example.m_commerce_admin.features.products.presentation.viewModel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.m_commerce_admin.features.products.data.mapper.toGraphQL
import com.example.m_commerce_admin.features.products.data.remote.ProductRemoteDataSource
import com.example.m_commerce_admin.features.products.data.remote.ProductRemoteDataSourceImpl
import com.example.m_commerce_admin.features.products.domain.entity.DomainProductInput
import com.example.m_commerce_admin.features.products.domain.entity.Product
import com.example.m_commerce_admin.features.products.domain.usecase.AddProductUseCase
import com.example.m_commerce_admin.features.products.domain.usecase.AddProductWithImagesParams
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
    private val remoteDataSource: ProductRemoteDataSource
) : ViewModel() {

    private val _productsState = MutableStateFlow<GetProductState>(GetProductState.Loading)
    val productsState: StateFlow<GetProductState> = _productsState

    private val currentList = mutableListOf<Product>()
    private var cursor: String? = null
    private var hasNextPage = true
    private var isPaginating = false

    private val _uiAddProductState = MutableStateFlow<AddProductState>(AddProductState.Idle)
    val uiAddProductState: StateFlow<AddProductState> = _uiAddProductState

    fun loadMoreProducts() {
        Log.d("ProductsViewModel", "loadMoreProducts called - isPaginating: $isPaginating, hasNextPage: $hasNextPage")
        if (isPaginating || !hasNextPage) {
            Log.d("ProductsViewModel", "Skipping loadMoreProducts - isPaginating: $isPaginating, hasNextPage: $hasNextPage")
            return
        }

        isPaginating = true
        Log.d("ProductsViewModel", "Starting pagination with cursor: $cursor")

        viewModelScope.launch {
            try {
                getAllProductsUseCase(GetProductsParams(first = 10, after = cursor)).collect { state ->
                    Log.d("ProductsViewModel", "Received state: $state")

                    when (state) {
                        is GetProductState.Success -> {
                            val newCursor = state.endCursor

                            // Avoid fetching the same page again
                            if (newCursor == cursor) {
                                Log.d("ProductsViewModel", "Same cursor received again, stopping pagination")
                                hasNextPage = false
                                isPaginating = false
                                return@collect
                            }

                            // Filter duplicates by product ID
                            val newProducts = state.data.filterNot { newProduct ->
                                currentList.any { it.id == newProduct.id }
                            }

                            if (newProducts.isEmpty()) {
                                Log.d("ProductsViewModel", "No new unique products found, stopping pagination")
                                hasNextPage = false
                                isPaginating = false
                                return@collect
                            }

                            currentList.addAll(newProducts)
                            cursor = newCursor
                            hasNextPage = state.hasNext

                            Log.d("ProductsViewModel", "Appended ${newProducts.size} new products, total: ${currentList.size}")
                            _productsState.value = GetProductState.Success(currentList.toList(), hasNextPage, cursor)
                        }

                        is GetProductState.Error -> {
                            Log.e("ProductsViewModel", "Error loading products: ${state.message}")
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
                Log.e("ProductsViewModel", "Exception in loadMoreProducts", e)
                _productsState.value = GetProductState.Error(e.message ?: "Unknown error")
            } finally {
                isPaginating = false
                Log.d("ProductsViewModel", "Pagination finished, isPaginating = false")
            }
        }
    }

    fun refreshProducts() {
        cursor = null
        hasNextPage = true
        currentList.clear()
        loadMoreProducts()
    }

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

                _uiAddProductState.value = if (result.isSuccess) {
                    AddProductState.Success
                } else {
                    val errorMessage = result.exceptionOrNull()?.message ?: "Unknown error occurred"
                    Log.e("ProductsViewModel", "Failed to add product with images: $errorMessage")
                    AddProductState.Error(errorMessage)
                }
            } catch (e: Exception) {
                Log.e("ProductsViewModel", "Exception during addProductWithImages", e)
                _uiAddProductState.value = AddProductState.Error(e.message ?: "An unexpected error occurred")
            }
        }
    }

    // Debug method to test upload process step by step
    fun debugUploadProcess(imageUris: List<Uri>, context: Context) {
        viewModelScope.launch {
            try {
                Log.d("DebugUpload", "🔍 Starting debug upload process")
                
                // Test 1: Prepare upload inputs
                val inputs = remoteDataSource.prepareStagedUploadInputs(context, imageUris)
                Log.d("DebugUpload", "✅ Step 1 - Prepared inputs: ${inputs.size} items")
                
                if (inputs.isEmpty()) {
                    Log.e("DebugUpload", "❌ No inputs prepared")
                    return@launch
                }
                
                // Test 2: Request staged uploads
                val targets = remoteDataSource.requestStagedUploads(inputs)
                Log.d("DebugUpload", "✅ Step 2 - Received targets: ${targets.size} items")
                
                if (targets.isEmpty()) {
                    Log.e("DebugUpload", "❌ No targets received")
                    return@launch
                }
                
                // Test 3: Try uploading first image
                val firstUri = imageUris.firstOrNull()
                val firstTarget = targets.firstOrNull()
                
                if (firstUri != null && firstTarget != null) {
                    Log.d("DebugUpload", "🔄 Testing upload for: ${firstUri.lastPathSegment}")
                    val uploadSuccess = remoteDataSource.uploadImageToStagedTarget(context, firstUri, firstTarget)
                    Log.d("DebugUpload", "✅ Step 3 - Upload result: $uploadSuccess")
                }
                
            } catch (e: Exception) {
                Log.e("DebugUpload", "❌ Debug process failed", e)
            }
        }
    }

}
