package com.example.m_commerce_admin.features.coupons.data.di

import com.apollographql.apollo.ApolloClient
import com.example.m_commerce_admin.features.coupons.data.remote.CouponRemoteDataSource
import com.example.m_commerce_admin.features.coupons.data.remote.CouponRemoteDataSourceImpl
import com.example.m_commerce_admin.features.coupons.data.remote.service.ShopifyCouponApi
import com.example.m_commerce_admin.features.coupons.data.repository.CouponRepositoryImpl
import com.example.m_commerce_admin.features.coupons.domain.repository.CouponRepository
import com.example.m_commerce_admin.features.coupons.domain.usecase.AddCouponUseCase
import com.example.m_commerce_admin.features.coupons.domain.usecase.GetAllCouponsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CouponProviderModule {
    @Provides
    @Singleton
    fun provideCouponRemoteDataSource(
        apolloClient: ApolloClient,
        shopifyCouponApi: ShopifyCouponApi
    ): CouponRemoteDataSource {
        return CouponRemoteDataSourceImpl(apolloClient, shopifyCouponApi)
    }


    @Provides
    @Singleton
    fun provideCouponRepository(remoteDataSource: CouponRemoteDataSource): CouponRepository {
        return CouponRepositoryImpl(remoteDataSource)
    }

    @Provides
    @Singleton
    fun provideGetAllCouponsUseCase(repository: CouponRepository): GetAllCouponsUseCase {
        return GetAllCouponsUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideAddCouponUseCase(repository: CouponRepository): AddCouponUseCase {
        return AddCouponUseCase(repository)
    }
} 