package com.example.m_commerce_admin.features.home.data.di

import com.apollographql.apollo.ApolloClient
import com.example.m_commerce_admin.features.home.data.remote.OrderRemoteDataSource
import com.example.m_commerce_admin.features.home.data.remote.OrderRemoteDataSourceImpl
import com.example.m_commerce_admin.features.home.domain.repository.OrderRepository
import com.example.m_commerce_admin.features.home.domain.usecase.GetLastOrdersUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object OrderProviderModule {

    @Provides
    @Singleton
    fun OrderRemoteDataSource(apolloClient: ApolloClient): OrderRemoteDataSource {
        return OrderRemoteDataSourceImpl(apolloClient)
    }

    @Provides
    @Singleton
    fun provideOrderUseCase(repo: OrderRepository): GetLastOrdersUseCase {
        return GetLastOrdersUseCase(repo)
    }


}