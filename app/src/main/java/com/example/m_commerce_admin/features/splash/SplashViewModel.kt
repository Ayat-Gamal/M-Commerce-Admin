package com.example.m_commerce_admin.features.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.m_commerce_admin.features.login.data.dataStore.AdminPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
@HiltViewModel
class SplashViewModel @Inject  constructor(

    adminPreferences: AdminPreferences
) : ViewModel() {

    val isLoggedIn = adminPreferences.isLoggedInFlow.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,  // Or SharingStarted.Lazily
        false
    )

}