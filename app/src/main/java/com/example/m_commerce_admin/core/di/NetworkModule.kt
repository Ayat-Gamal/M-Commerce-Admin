package com.example.m_commerce_admin.core.di

import com.apollographql.apollo.ApolloClient
import com.example.m_commerce_admin.BuildConfig
import com.example.m_commerce_admin.features.coupons.data.remote.service.ShopifyCouponApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header("User-Agent", "MCommerceAdmin/1.0")
                    .header("Accept", "*/*")
                chain.proceed(requestBuilder.build())
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideApolloClient(): ApolloClient {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        return ApolloClient.Builder()
            .serverUrl(BuildConfig.shopDomain)
            .addHttpHeader("X-Shopify-Access-Token", BuildConfig.adminToken)
            .addHttpHeader("Content-Type", "application/json")
            .build()
    }


    @Provides
    @Singleton
    fun provideShopifyCouponApi(): ShopifyCouponApi {
        return Retrofit.Builder()
            .baseUrl("https://mad45-alex-and02.myshopify.com/admin/api/2024-04/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder().addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("X-Shopify-Access-Token", BuildConfig.adminToken)
                        .build()
                    chain.proceed(request)
                }.build()
            )
            .build()
            .create(ShopifyCouponApi::class.java)
    }
}
