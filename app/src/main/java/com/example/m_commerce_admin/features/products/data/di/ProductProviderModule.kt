package com.example.m_commerce_admin.features.products.data.di

import com.apollographql.apollo.ApolloClient
import com.example.m_commerce_admin.features.products.data.remote.ProductRemoteDataSource
import com.example.m_commerce_admin.features.products.data.remote.ProductRemoteDataSourceImpl
import com.example.m_commerce_admin.features.products.data.repository.rest.RestProductRepositoryImpl
import com.example.m_commerce_admin.features.products.data.retrofitRemote.RetrofitProductDataSource
import com.example.m_commerce_admin.features.products.data.retrofitRemote.RetrofitProductDataSourceImpl
import com.example.m_commerce_admin.features.products.data.retrofitRemote.ShopifyProductApi
import com.example.m_commerce_admin.features.products.domain.repository.ProductRepository
import com.example.m_commerce_admin.features.products.domain.repository.RestProductRepository
import com.example.m_commerce_admin.features.products.domain.usecase.*
import com.example.m_commerce_admin.features.products.domain.usecase.rest.AddRestProductWithImagesUseCase
import com.example.m_commerce_admin.features.products.domain.usecase.rest.CreateRestProductUseCase
import com.example.m_commerce_admin.features.products.domain.usecase.rest.DeleteRestProductUseCase
import com.example.m_commerce_admin.features.products.domain.usecase.rest.GetAllRestProductsUseCase
import com.example.m_commerce_admin.features.products.domain.usecase.rest.SetInventoryLevelUseCase
import com.example.m_commerce_admin.features.products.domain.usecase.rest.UpdateRestProductUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProductProviderModule {

    // GraphQL Data Source
    @Provides
    @Singleton
    fun ProductRemoteDataSource(apolloClient: ApolloClient, okHttpClient: OkHttpClient): ProductRemoteDataSource {
        return ProductRemoteDataSourceImpl(apolloClient, okHttpClient)
    }

    // REST API Data Source
    @Provides
    @Singleton
    fun provideRetrofitProductDataSource(api: ShopifyProductApi, graph:ApolloClient): RetrofitProductDataSource {
        return RetrofitProductDataSourceImpl(api,graph)
    }

    // REST API Repository
    @Provides
    @Singleton
    fun provideRestProductRepository(dataSource: RetrofitProductDataSource, okHttpClient: OkHttpClient): RestProductRepository {
        return RestProductRepositoryImpl(dataSource, okHttpClient)
    }

    // GraphQL Use Cases
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

    // REST API Use Cases
    @Provides
    @Singleton
    fun provideGetAllRestProductsUseCase(repo: RestProductRepository): GetAllRestProductsUseCase {
        return GetAllRestProductsUseCase(repo)
    }

    @Provides
    @Singleton
    fun provideCreateRestProductUseCase(repo: RestProductRepository): CreateRestProductUseCase {
        return CreateRestProductUseCase(repo)
    }

    @Provides
    @Singleton
    fun provideUpdateRestProductUseCase(repo: RestProductRepository): UpdateRestProductUseCase {
        return UpdateRestProductUseCase(repo)
    }

    @Provides
    @Singleton
    fun provideDeleteRestProductUseCase(repo: RestProductRepository): DeleteRestProductUseCase {
        return DeleteRestProductUseCase(repo)
    }

    @Provides
    @Singleton
    fun provideUploadProductImagesUseCase(repo: RestProductRepository): UploadProductImagesUseCase {
        return UploadProductImagesUseCase(repo)
    }

    @Provides
    @Singleton
    fun provideAddRestProductWithImagesUseCase(repo: RestProductRepository): AddRestProductWithImagesUseCase {
        return AddRestProductWithImagesUseCase(repo)
    }

    @Provides
    @Singleton
    fun providePublishProductUseCase(repo: RestProductRepository): PublishProductUseCase {
        return PublishProductUseCase(repo)
    }
    @Provides
    @Singleton
    fun provideSetInventoryLevelUseCase (repo: RestProductRepository): SetInventoryLevelUseCase {
        return SetInventoryLevelUseCase (repo)
    }
}