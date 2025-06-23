package com.example.m_commerce_admin.features.products.data.repository

import com.example.m_commerce_admin.features.products.data.remote.ProductRemoteDataSource
import com.example.m_commerce_admin.features.products.domain.repository.ProductRepository
import com.example.m_commerce_admin.features.products.presentation.ProductState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val remoteDataSource: ProductRemoteDataSource
) : ProductRepository {

    override fun getProducts(first: Int, after: String?): Flow<ProductState> {
        return remoteDataSource.getProducts(first, after)
    }
}
