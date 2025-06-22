package com.example.m_commerce_admin.core.di

import android.content.Context
import com.example.m_commerce_admin.features.login.data.dataStore.AdminPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UserPreferencesModule {
    @Provides
    @Singleton
    fun provideUserPreferences(@ApplicationContext context: Context): AdminPreferences {
        return AdminPreferences(context)
    }

}
