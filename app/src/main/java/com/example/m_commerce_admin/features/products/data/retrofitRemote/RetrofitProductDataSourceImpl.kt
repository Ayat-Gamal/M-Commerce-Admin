package com.example.m_commerce_admin.features.products.data.retrofitRemote

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RetrofitProductDataSourceImpl  @Inject constructor(
    private val api: ShopifyProductApi
) : RetrofitProductDataSource {

    override fun getProductList(): Flow<List<ProductDto>> {
        return TODO() //api.getAllProducts().products.map { it.toDomain() }
    }
}