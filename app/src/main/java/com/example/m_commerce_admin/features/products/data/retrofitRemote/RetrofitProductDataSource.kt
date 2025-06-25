package com.example.m_commerce_admin.features.products.data.retrofitRemote

import kotlinx.coroutines.flow.Flow

interface RetrofitProductDataSource {

    fun getProductList(): Flow<List<ProductDto>>

}

