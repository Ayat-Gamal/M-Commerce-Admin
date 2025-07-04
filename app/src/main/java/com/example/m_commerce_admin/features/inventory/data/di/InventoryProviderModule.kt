package com.example.m_commerce_admin.features.inventory.data.di

import com.example.m_commerce_admin.features.inventory.data.remote.InventoryRemoteDataSource
import com.example.m_commerce_admin.features.inventory.data.remote.InventoryRemoteDataSourceImpl
import com.example.m_commerce_admin.features.inventory.data.remote.service.ShopifyInventoryApi
import com.example.m_commerce_admin.features.inventory.domain.repository.InventoryRepository
import com.example.m_commerce_admin.features.inventory.domain.usecase.AdjustInventoryUseCase
import com.example.m_commerce_admin.features.inventory.domain.usecase.GetInventoryLevelsUseCase
import com.example.m_commerce_admin.features.inventory.domain.usecase.GetProductsForInventoryUseCase
import com.example.m_commerce_admin.features.products.domain.repository.RestProductRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object InventoryProviderModule {

    @Provides
    @Singleton
    fun provideGetInventoryLevelsUseCase(repository: InventoryRepository): GetInventoryLevelsUseCase {
        return GetInventoryLevelsUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetAdjustInventoryUseCase(repository: InventoryRepository): AdjustInventoryUseCase {
        return AdjustInventoryUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetProductsForInventoryUseCase(productRepository: RestProductRepository): GetProductsForInventoryUseCase {
        return GetProductsForInventoryUseCase(productRepository)
    }

    @Provides
    @Singleton
    fun provideInventoryRemoteDataSource(
        shopifyInventoryApi: ShopifyInventoryApi
    ): InventoryRemoteDataSource {
        return InventoryRemoteDataSourceImpl(shopifyInventoryApi)
    }

}