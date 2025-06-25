package com.example.m_commerce_admin.features.coupons.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.background
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.m_commerce_admin.config.routes.AppRoutes
import com.example.m_commerce_admin.features.coupons.domain.entity.CouponItem
import com.example.m_commerce_admin.features.coupons.presentation.component.CouponCard
import com.example.m_commerce_admin.features.coupons.presentation.viewModel.CouponsViewModel
import com.example.m_commerce_admin.features.coupons.presentation.states.AddCouponState
import com.example.m_commerce_admin.features.coupons.presentation.states.UpdateCouponState
import com.example.m_commerce_admin.features.coupons.presentation.states.DeleteCouponState
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CouponScreenUI(
    modifier: Modifier = Modifier,
    viewModel: CouponsViewModel = hiltViewModel(),
    navController: NavController? = null
) {
    val coupons = viewModel.coupons.collectAsState().value
    val addCouponState = viewModel.addCouponState.collectAsState().value
    val updateCouponState = viewModel.updateCouponState.collectAsState().value
    val deleteCouponState = viewModel.deleteCouponState.collectAsState().value
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(Unit) {
        viewModel.fetchAllCoupons()
    }
    
    LaunchedEffect(addCouponState) {
        if (addCouponState is AddCouponState.Success) {
            viewModel.fetchAllCoupons()
            scope.launch {
                snackbarHostState.showSnackbar("Coupon added successfully!")
            }
            viewModel.resetAddCouponState()
        }
    }

    LaunchedEffect(updateCouponState) {
        if (updateCouponState is UpdateCouponState.Success) {
            viewModel.fetchAllCoupons()
            scope.launch {
                snackbarHostState.showSnackbar("Coupon updated successfully!")
            }
            viewModel.resetUpdateCouponState()
        }
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
                    val result = snackbarHostState.showSnackbar(
                        message = deleteCouponState.message,
                        actionLabel = "Retry"
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.retryLastDeleteOperation()
                    } else {
                        viewModel.resetDeleteCouponState()
                    }
                }
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                item {
                    Spacer(modifier = modifier.padding(top = 8.dp))
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // TODO: Add a search field or Add button
                    }
                }

                items(coupons) { coupon ->
                    CouponCard(
                        coupon = coupon,
                        onEditClick = { navController?.navigate(AppRoutes.AddCouponForm(coupon.id)) },
                        onDeleteClick = {
                            viewModel.deleteCoupon(coupon.code)
                        }
                    )
                }
            }

            // Loading overlay for delete operation
            if (deleteCouponState is DeleteCouponState.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.material3.Card(
                        modifier = Modifier.padding(16.dp),
                        colors = androidx.compose.material3.CardDefaults.cardColors(androidx.compose.ui.graphics.Color.White)
                    ) {
                        androidx.compose.foundation.layout.Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(16.dp))
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
}
