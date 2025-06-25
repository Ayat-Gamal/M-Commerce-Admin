package com.example.m_commerce_admin.features.inventory.data.di

import com.example.m_commerce_admin.features.inventory.data.repository.InventoryRepositoryImpl
import com.example.m_commerce_admin.features.inventory.domain.repository.InventoryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class InventoryBindsModule {
    @Binds
    abstract fun bindInventoryRepository(
        impl: InventoryRepositoryImpl
    ): InventoryRepository

}

