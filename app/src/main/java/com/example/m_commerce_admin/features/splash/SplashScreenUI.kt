package com.example.m_commerce_admin.features.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.m_commerce_admin.R
import com.example.m_commerce_admin.config.routes.AppRoutes
import com.example.m_commerce_admin.config.theme.SplashTeal
import com.example.m_commerce_admin.features.home.presentation.viewModel.HomeViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreenUI(
    viewModel: SplashViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel(),

    navigate: (AppRoutes) -> Unit
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1,
        speed = 1.0f,
        isPlaying = true,
        restartOnPlay = false
    )

    LaunchedEffect(progress) {
        homeViewModel.fetchOrders()
        if (progress == 1f) {
            delay(300)
            if (isLoggedIn) {
                navigate(AppRoutes.HomeScreen)
            } else {
                navigate(AppRoutes.LoginScreen)
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = progress < 1f,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column {
                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier.size(200.dp)
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Dashboard",
                    style = TextStyle(
                        color = SplashTeal,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold,

                        )
                )
            }

        }
    }
}




