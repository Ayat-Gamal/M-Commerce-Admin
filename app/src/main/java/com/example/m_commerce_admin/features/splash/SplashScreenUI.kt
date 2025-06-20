package com.example.m_commerce_admin.features.splash

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.m_commerce_admin.config.routes.AppRoutes
import kotlinx.coroutines.delay

@Composable
fun SplashScreenUI(
    viewModel: SplashViewModel = hiltViewModel(),
    navigate: (AppRoutes) -> Unit
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()

    LaunchedEffect(isLoggedIn) {
        delay(1000L)
        if (isLoggedIn) {
             navigate(AppRoutes.HomeScreen)
        } else {
             navigate(AppRoutes.LoginScreen)
        }
    }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Checking Login...")
        }
    }



