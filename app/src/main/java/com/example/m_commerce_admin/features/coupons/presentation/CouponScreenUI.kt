package com.example.m_commerce_admin.features.coupons.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.m_commerce_admin.config.routes.AppRoutes
import com.example.m_commerce_admin.config.theme.DarkestGray
import com.example.m_commerce_admin.core.shared.components.states.Empty
import com.example.m_commerce_admin.core.shared.components.states.NoNetwork
import com.example.m_commerce_admin.features.coupons.presentation.component.CouponCard
import com.example.m_commerce_admin.features.coupons.presentation.component.CouponSearchBar
import com.example.m_commerce_admin.features.coupons.presentation.states.DeleteCouponState
import com.example.m_commerce_admin.features.coupons.presentation.viewModel.CouponsViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CouponScreenUI(
    modifier: Modifier = Modifier,
    viewModel: CouponsViewModel = hiltViewModel(),
    navController: NavController? = null
) {
    val coupons = viewModel.coupons.collectAsState().value
    val searchQuery = viewModel.searchQuery.collectAsState().value
    val selectedFilter = viewModel.selectedFilter.collectAsState().value
    val deleteCouponState = viewModel.deleteCouponState.collectAsState().value
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.fetchAllCoupons()
    }

    LaunchedEffect(deleteCouponState) {
        when (deleteCouponState) {
            is DeleteCouponState.Success -> {
                viewModel.fetchAllCoupons()
                scope.launch {
                    snackbarHostState.showSnackbar("Coupon deleted successfully!")
                }
                viewModel.resetDeleteCouponState()
            }

            is DeleteCouponState.Error -> {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = deleteCouponState.message,
                    )
                    viewModel.resetDeleteCouponState()
                }
            }

            else -> {
                // Do nothing for Loading and Idle states
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                modifier = Modifier.navigationBarsPadding(),
                hostState = snackbarHostState
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search and Filter Bar
            CouponSearchBar(
                searchQuery = searchQuery,
                selectedFilter = selectedFilter,
                onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                onFilterChange = { viewModel.updateFilter(it) },
                onClearFilters = { viewModel.clearFilters() }
            )

            // Results Summary
            if (searchQuery.isNotEmpty() || selectedFilter != com.example.m_commerce_admin.features.coupons.presentation.viewModel.CouponFilter.ALL) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${coupons.size} coupon${if (coupons.size != 1) "s" else ""} found",
                        fontSize = 14.sp,
                        color = DarkestGray,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Coupons List
            if (coupons.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (searchQuery.isNotEmpty() || selectedFilter != com.example.m_commerce_admin.features.coupons.presentation.viewModel.CouponFilter.ALL) {
                        Empty("No coupons match your search criteria")
                    } else {
                        Empty("No coupons available")
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    items(coupons) { coupon ->
                        CouponCard(
                            coupon = coupon,
                            onEditClick = {
                                navController?.navigate(
                                    AppRoutes.UpdateCouponForm(
                                        coupon.id
                                    )
                                )
                            },
                            onDeleteClick = {
                                viewModel.deleteCoupon(coupon.code)
                            }
                        )
                    }
                }
            }
        }

        // Delete Loading Overlay
        if (deleteCouponState is DeleteCouponState.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material3.Card(
                    modifier = Modifier.padding(16.dp),
                    colors = cardColors(androidx.compose.ui.graphics.Color.White)
                ) {
                    androidx.compose.foundation.layout.Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.padding(16.dp))
                        Text(
                            text = "Deleting coupon...",
                            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
