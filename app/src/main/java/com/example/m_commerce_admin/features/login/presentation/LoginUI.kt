package com.example.m_commerce_admin.features.login.presentation


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.m_commerce_admin.config.routes.AppRoutes
import com.example.m_commerce_admin.features.login.presentation.component.LoginFormSection
import com.example.m_commerce_admin.features.login.presentation.component.LoginHeaderSection
import kotlinx.coroutines.launch

@Composable
fun LoginScreenUI(
    viewModel: LoginViewModel = hiltViewModel(),
    snackBarHostState: SnackbarHostState,
    navigate: (AppRoutes) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val message by viewModel.messageState.collectAsStateWithLifecycle()


    LaunchedEffect(message) {
        if (message.isNotBlank()) {
            snackBarHostState.currentSnackbarData?.dismiss()
            snackBarHostState.showSnackbar(message)
            viewModel.clearMessage()
        }
    }

    when (uiState) {
        is LoginState.Error -> {
            LaunchedEffect(Unit) {
                snackBarHostState.currentSnackbarData?.dismiss()
                scope.launch {
                    snackBarHostState.showSnackbar((uiState as LoginState.Error).message)
                }
            }
        }

        is LoginState.Idle -> {
            LaunchedEffect(Unit) {
                scope.launch {
                    snackBarHostState.showSnackbar("Hi, Admin", duration = SnackbarDuration.Short)
                }
            }
        }

        is LoginState.Success -> {
            LaunchedEffect(Unit) {
                snackBarHostState.currentSnackbarData?.dismiss()

                scope.launch {

                    snackBarHostState.showSnackbar(
                        "Logged In Successful",
                        duration = SnackbarDuration.Short
                    )
                    navigate(AppRoutes.HomeScreen)
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item { LoginHeaderSection() }

            item { LoginFormSection() }

        }

    }
}
