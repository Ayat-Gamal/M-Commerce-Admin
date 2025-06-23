package com.example.m_commerce_admin.features.products.data.remote

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.example.m_commerce_admin.GetProductsQuery
import com.example.m_commerce_admin.features.products.data.mapper.toDomain
import com.example.m_commerce_admin.features.products.presentation.ProductState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ProductRemoteDataSourceImpl @Inject constructor(
    private val apolloClient: ApolloClient
) : ProductRemoteDataSource {

    override fun getProducts(first: Int, after: String?): Flow<ProductState> = flow {
        emit(ProductState.Loading)

        val response = apolloClient.query(
            GetProductsQuery(first = first, after = Optional.presentIfNotNull(after))
        ).execute()

        val products = response.data?.products?.edges?.map { edge ->
            edge.node.toDomain()
        } ?: emptyList()

        val hasNextPage = response.data?.products?.pageInfo?.hasNextPage ?: false
        val endCursor = response.data?.products?.pageInfo?.endCursor

        emit(ProductState.Success(products, hasNextPage, endCursor))
    }
}
