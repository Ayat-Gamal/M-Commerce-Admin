package com.example.m_commerce_admin.features.app

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.m_commerce_admin.R
import com.example.m_commerce_admin.config.connectivity.ConnectivityObserverImpl
import com.example.m_commerce_admin.config.routes.AppRoutes
import com.example.m_commerce_admin.config.routes.NavSetup
import com.example.m_commerce_admin.config.theme.MCommerceAdminTheme
import com.example.m_commerce_admin.core.helpers.navigateAndClear
import com.example.m_commerce_admin.core.shared.components.bottom_nav_bar.BottomNavigationBar
import com.example.m_commerce_admin.features.app.component.getFABForRouteWithAction
import com.example.m_commerce_admin.features.app.component.getTopAppBarForRoute
import com.example.m_commerce_admin.features.app.viewModel.ConnectivityViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        lateinit var showBottomNavbar: MutableState<Boolean>

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            MCommerceAdminTheme {
                val viewModelConnectivity = viewModel<ConnectivityViewModel> {
                    ConnectivityViewModel(
                        connectivityObserver = ConnectivityObserverImpl(
                            context = applicationContext
                        )
                    )
                }
                val isConnected by viewModelConnectivity.isConnected.collectAsStateWithLifecycle()

                showBottomNavbar = remember { mutableStateOf(false) }
                Main(showBottomNavbar = showBottomNavbar, isConnected)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Main(showBottomNavbar: MutableState<Boolean>, isConnected: Boolean) {
    val navController = rememberNavController()
    val snackBarHostState = remember { SnackbarHostState() }
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination

    val topBar = getTopAppBarForRoute(
        currentDestination = currentRoute,
        toLogin = {
            navController.navigateAndClear(AppRoutes.LoginScreen)
        }
    )

    val fabComposable = getFABForRouteWithAction(
        currentDestination = currentRoute,
        navController,

        )

    Scaffold(
        modifier = Modifier.safeContentPadding(),

        topBar = {
            if (isConnected) {
                topBar?.invoke()
            }
        },

        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        },

        floatingActionButton = {
            if (isConnected) {
                fabComposable?.invoke()
            }

        },

        bottomBar = {
            if (showBottomNavbar.value) {
                BottomNavigationBar(navController)
            } else null
        }
    ) { innerPadding ->

        val bottomPadding = Modifier.padding(
            start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
            top = innerPadding.calculateTopPadding(),
            end = innerPadding.calculateEndPadding(LayoutDirection.Ltr),
            bottom = 16.dp
        )

        NavSetup(
            navController,
            snackBarHostState,
            modifier = bottomPadding,
            showBottomNavbar = showBottomNavbar,
            isConnected = isConnected,
        )

    }
}
