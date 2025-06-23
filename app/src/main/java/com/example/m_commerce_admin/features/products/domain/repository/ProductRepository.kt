package com.example.m_commerce_admin.features.products.domain.repository

import com.example.m_commerce_admin.features.home.domain.entity.Order
import com.example.m_commerce_admin.features.home.presentation.HomeState
import com.example.m_commerce_admin.features.products.domain.entity.Product
import com.example.m_commerce_admin.features.products.presentation.ProductState
import kotlinx.coroutines.flow.Flow

interface ProductRepository {

    fun getProducts(first: Int, after: String?): Flow<ProductState>

}