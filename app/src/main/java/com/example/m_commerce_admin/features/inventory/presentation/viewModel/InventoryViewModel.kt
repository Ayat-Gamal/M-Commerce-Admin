package com.example.m_commerce_admin.features.inventory.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.m_commerce_admin.features.inventory.domain.entity.InventoryLevel
import com.example.m_commerce_admin.features.inventory.domain.usecase.AdjustInventoryUseCase
import com.example.m_commerce_admin.features.inventory.domain.usecase.GetInventoryLevelsUseCase
import com.example.m_commerce_admin.features.inventory.domain.usecase.GetProductsForInventoryUseCase
import com.example.m_commerce_admin.features.inventory.domain.usecase.GetProductsForInventoryParams
import com.example.m_commerce_admin.features.inventory.presentation.state.InventoryLevelsState
import com.example.m_commerce_admin.features.products.domain.entity.rest.RestProduct
import com.example.m_commerce_admin.features.products.domain.entity.rest.RestProductImage
import com.example.m_commerce_admin.features.products.domain.entity.rest.RestProductVariant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val getInventoryLevelsUseCase: GetInventoryLevelsUseCase,
    private val adjustInventoryUseCase: AdjustInventoryUseCase,
    private val getProductsForInventoryUseCase: GetProductsForInventoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<InventoryLevelsState>(InventoryLevelsState.Loading)
    val uiState: StateFlow<InventoryLevelsState> = _uiState.asStateFlow()
    
    private val _adjustState = MutableStateFlow<Result<InventoryLevel>?>(null)
    val adjustState: StateFlow<Result<InventoryLevel>?> = _adjustState

    private val _stockLevel = MutableStateFlow<List<InventoryLevel>?>(null)
    val stockLevel: StateFlow<List<InventoryLevel>?> = _stockLevel

    // Filtering and search state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedFilter = MutableStateFlow(InventoryFilter.ALL)
    val selectedFilter: StateFlow<InventoryFilter> = _selectedFilter.asStateFlow()

    private val _allInventoryItems = MutableStateFlow<List<InventoryLevel>>(emptyList())
    private val _filteredInventoryItems = MutableStateFlow<List<InventoryLevel>>(emptyList())

    init {
        loadInventoryData()
        getLowStock()
    }

    fun getLowStock() {
        viewModelScope.launch {
            getInventoryLevelsUseCase()
                .collect { result ->
                    result
                        .onSuccess { levels ->
                            if (levels.isEmpty()) {
                                _stockLevel.emit(emptyList())
                            } else {
                                val enhancedLevels = enhanceInventoryWithProductData(levels)
                                _stockLevel.emit(enhancedLevels.filter { it.available < 5 })
                            }
                        }
                        .onFailure { throwable ->
                            val errorMessage =
                                throwable.localizedMessage ?: "An unknown error occurred."
                            _stockLevel.emit(emptyList())
                        }
                }
        }
    }

    fun adjustInventoryLevel(inventoryItemId: Long, adjustment: Int) {
        viewModelScope.launch {
            try {
                val level = adjustInventoryUseCase(inventoryItemId, adjustment)
                _adjustState.value = Result.success(level)
                refreshInventoryData()

            } catch (e: Exception) {
                _adjustState.value = Result.failure(e)
            }
        }
    }

    fun loadInventoryData() {
        viewModelScope.launch {
            _uiState.value = InventoryLevelsState.Loading

            getInventoryLevelsUseCase()
                .collect { result ->
                    result
                        .onSuccess { levels ->
                            if (levels.isEmpty()) {
                                _uiState.value = InventoryLevelsState.Empty
                            } else {
                                val enhancedLevels = enhanceInventoryWithProductData(levels)
                                _allInventoryItems.value = enhancedLevels
                                applyFilters()
                            }
                        }
                        .onFailure { throwable ->
                            val errorMessage =
                                throwable.localizedMessage ?: "An unknown error occurred."
                            _uiState.value = InventoryLevelsState.Error(errorMessage)
                        }
                }
        }
    }

    // Filtering functions
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        applyFilters()
    }

    fun updateFilter(filter: InventoryFilter) {
        _selectedFilter.value = filter
        applyFilters()
    }

    fun clearFilters() {
        _searchQuery.value = ""
        _selectedFilter.value = InventoryFilter.ALL
        applyFilters()
    }

    private fun applyFilters() {
        val query = _searchQuery.value.lowercase()
        val filter = _selectedFilter.value
        val allItems = _allInventoryItems.value

        val filtered = allItems.filter { item ->
            val matchesSearch = query.isEmpty() || 
                (item.productTitle?.lowercase()?.contains(query) == true) ||
                (item.productSku?.lowercase()?.contains(query) == true) ||
                item.inventoryItemId.toString().contains(query)
            
            val matchesFilter = when (filter) {
                InventoryFilter.ALL -> true
                InventoryFilter.LOW_STOCK -> item.available < 5
                InventoryFilter.OUT_OF_STOCK -> item.available == 0
                InventoryFilter.IN_STOCK -> item.available > 0
            }
            
            matchesSearch && matchesFilter
        }

        _filteredInventoryItems.value = filtered
        
        when {
            filtered.isEmpty() && query.isNotEmpty() -> {
                _uiState.value = InventoryLevelsState.Empty
            }
            filtered.isEmpty() -> {
                _uiState.value = InventoryLevelsState.Empty
            }
            else -> {
                _uiState.value = InventoryLevelsState.Success(filtered)
            }
        }
    }

    fun refreshInventoryData() {
        loadInventoryData()
        getLowStock()
    }

    private suspend fun enhanceInventoryWithProductData(inventoryLevels: List<InventoryLevel>): List<InventoryLevel> {
        return try {
            val productsResult = getProductsForInventoryUseCase(
                GetProductsForInventoryParams(limit = 250, pageInfo = null, status = null)
            ).first()
            
            if (productsResult.isFailure) {
                return inventoryLevels // Return original data if product fetch fails
            }
            
            val products = productsResult.getOrThrow()
            
            // Build mapping
            val variantMap = mutableMapOf<Long, Triple<RestProduct, RestProductVariant, RestProductImage?>>()
            for (product in products) {
                val image = product.images?.firstOrNull()
                for (variant in product.variants) {
                    variantMap[variant.inventoryItemId] = Triple(product, variant, image)
                }
            }
            
            // Enhance inventory levels
            inventoryLevels.map { level ->
                val triple = variantMap[level.inventoryItemId]
                val product = triple?.first
                val variant = triple?.second
                val image = triple?.third
                
                InventoryLevel(
                    inventoryItemId = level.inventoryItemId,
                    locationId = level.locationId,
                    available = level.available,
                    updatedAt = level.updatedAt,
                    graphqlApiId = level.graphqlApiId,
                    productTitle = product?.title ?: "Product ${level.inventoryItemId}",
                    productImage = image?.src,
                    productPrice = variant?.price ?: "0.00",
                    productSku = variant?.sku ?: "SKU-${level.inventoryItemId}"
                )
            }
        } catch (e: Exception) {
            inventoryLevels // Return original data if enhancement fails
        }
    }
}

enum class InventoryFilter(val displayName: String) {
    ALL("All Items"),
    LOW_STOCK("Low Stock (< 5)"),
    OUT_OF_STOCK("Out of Stock"),
    IN_STOCK("In Stock")
}
