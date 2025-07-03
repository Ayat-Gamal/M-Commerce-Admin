package com.example.m_commerce_admin.features.home.data.di

import com.example.m_commerce_admin.features.home.data.repository.OrderRepositoryImpl
import com.example.m_commerce_admin.features.home.domain.repository.OrderRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class OrderBindsModule {
    @Singleton
    @Binds
    abstract fun provideOrderRepository(repo: OrderRepositoryImpl): OrderRepository

}


