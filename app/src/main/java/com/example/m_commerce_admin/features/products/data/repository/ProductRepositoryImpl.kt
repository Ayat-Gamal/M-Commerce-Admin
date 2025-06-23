package com.example.m_commerce_admin.features.products.data.repository

import com.example.m_commerce_admin.features.products.data.remote.ProductRemoteDataSource
import com.example.m_commerce_admin.features.products.domain.entity.DomainProductInput
import com.example.m_commerce_admin.features.products.domain.repository.ProductRepository
import com.example.m_commerce_admin.features.products.presentation.states.GetProductState
import com.example.m_commerce_admin.type.ProductInput
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val remoteDataSource: ProductRemoteDataSource
) : ProductRepository {

    override fun getProducts(first: Int, after: String?): Flow<GetProductState> {
        return remoteDataSource.getProducts(first, after)
    }

    override suspend fun addProduct(product: ProductInput): Result<Unit> {
        return remoteDataSource.addProduct(product)
    }

    override suspend fun addProductWithImages(product: DomainProductInput, imageUris: List<String>): Result<Unit> {
        return remoteDataSource.addProductWithImages(product, imageUris)
    }

}
