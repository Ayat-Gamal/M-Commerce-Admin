package com.example.m_commerce_admin.features.products.data.di

import com.apollographql.apollo.ApolloClient
import com.example.m_commerce_admin.features.products.data.remote.ProductRemoteDataSource
import com.example.m_commerce_admin.features.products.data.remote.ProductRemoteDataSourceImpl
import com.example.m_commerce_admin.features.products.domain.repository.ProductRepository
import com.example.m_commerce_admin.features.products.domain.usecase.AddProductUseCase
import com.example.m_commerce_admin.features.products.domain.usecase.DeleteProductUseCase
import com.example.m_commerce_admin.features.products.domain.usecase.GetAllProductsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProductProviderModule {

    @Provides
    @Singleton
    fun ProductRemoteDataSource(apolloClient: ApolloClient,okHttpClient: OkHttpClient): ProductRemoteDataSource {
        return ProductRemoteDataSourceImpl(apolloClient,okHttpClient)
    }

    @Provides
    @Singleton
    fun provideGetProductUseCase(repo: ProductRepository): GetAllProductsUseCase {
        return GetAllProductsUseCase(repo)
    }
    @Provides
    @Singleton
    fun provideAddProductUseCase(repo: ProductRepository): AddProductUseCase {
        return AddProductUseCase(repo)
    }
    @Provides
    @Singleton
    fun provideDeleteProductUseCase(repo: ProductRepository): DeleteProductUseCase {
        return DeleteProductUseCase(repo)
    }
}