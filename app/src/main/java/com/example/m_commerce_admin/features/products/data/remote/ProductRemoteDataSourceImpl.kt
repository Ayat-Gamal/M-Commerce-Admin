package com.example.m_commerce_admin.features.products.data.remote

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.example.m_commerce_admin.AddProductMutation
import com.example.m_commerce_admin.GetProductsQuery
import com.example.m_commerce_admin.features.products.data.mapper.toDomain
import com.example.m_commerce_admin.features.products.presentation.states.GetProductState
import com.example.m_commerce_admin.type.ProductInput
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ProductRemoteDataSourceImpl @Inject constructor(
    private val apolloClient: ApolloClient
) : ProductRemoteDataSource {

    override fun getProducts(first: Int, after: String?): Flow<GetProductState> = flow {
        emit(GetProductState.Loading)

        val response = apolloClient.query(
            GetProductsQuery(first = first, after = Optional.presentIfNotNull(after))
        ).execute()

        val products = response.data?.products?.edges?.map { edge ->
            edge.node.toDomain()
        } ?: emptyList()

        val hasNextPage = response.data?.products?.pageInfo?.hasNextPage ?: false
        val endCursor = response.data?.products?.pageInfo?.endCursor

        emit(GetProductState.Success(products, hasNextPage, endCursor))
    }


    override suspend fun addProduct(product: ProductInput): Result<Unit> {
        return try {
            val gqlInput = ProductInput(
                title = product.title,
                descriptionHtml = product.descriptionHtml,
                productType = product.productType,
                vendor = product.vendor,
                status = product.status
            )

            val response = apolloClient.mutation(AddProductMutation(gqlInput)).execute()

            val errors = response.data?.productCreate?.userErrors
            if (!errors.isNullOrEmpty()) {
                Result.failure(Exception(errors.first().message))
            } else {
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}




