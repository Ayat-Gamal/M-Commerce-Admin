package com.example.m_commerce_admin.features.home.data.remote

import com.apollographql.apollo.ApolloClient
import com.example.m_commerce_admin.GetLastOrdersQuery
import com.example.m_commerce_admin.features.home.domain.entity.Order
import com.example.m_commerce_admin.features.home.presentation.HomeState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class OrderRemoteDataSourceImpl @Inject constructor(private val apolloClient: ApolloClient) :
    OrderRemoteDataSource {
    override fun getLastOrders(): Flow<HomeState<List<Order>>> = flow {
        emit(HomeState.Loading)
        try {
            val response = apolloClient.query(GetLastOrdersQuery()).execute()
            val orderList = response.data?.orders?.edges?.mapNotNull { edge ->
                edge?.node?.let { node ->
                    Order(
                        id = node.id,
                        name = node.name ?: "Order Code",
                        totalAmount = node.totalPriceSet.shopMoney.amount.toString() ,
                        currency = (node.totalPriceSet?.shopMoney?.currencyCode
                            ?: "EGP").toString(),
                        createdAt = node.createdAt.toString(),
                        customerName = node.customer?.displayName,
                        customerEmail = node.customer?.email,
                        status = node.displayFulfillmentStatus?.name
                    )

                }

            } ?: emptyList()
            emit(HomeState.Success(orderList))

        } catch (e: Exception) {
            e.printStackTrace()
            emit(HomeState.Error(e.message ?: "Something went Wrong"))
        }


    }
}