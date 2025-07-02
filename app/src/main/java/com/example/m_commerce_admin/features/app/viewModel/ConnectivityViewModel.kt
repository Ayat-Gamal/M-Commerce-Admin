package com.example.m_commerce_admin.features.app.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.m_commerce_admin.config.connectivity.ConnectivityObserver
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class ConnectivityViewModel(
    private val connectivityObserver: ConnectivityObserver
) :ViewModel(){
    val isConnected = connectivityObserver
        .isConnected
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            false
        )

}