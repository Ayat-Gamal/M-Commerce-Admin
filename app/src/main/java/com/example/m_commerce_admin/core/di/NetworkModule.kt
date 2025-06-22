package com.example.m_commerce_admin.core.di

import com.apollographql.apollo.ApolloClient
import com.example.m_commerce_admin.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideApolloClient(): ApolloClient {
        return ApolloClient.Builder()
            .serverUrl(BuildConfig.shopDomain)
            .addHttpHeader("X-Shopify-Access-Token", BuildConfig.adminToken)
            .addHttpHeader("Content-Type", "application/json")
            .build()
    }
}
