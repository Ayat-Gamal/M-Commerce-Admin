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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.m_commerce_admin.features.coupons.domain.entity.CouponItem
import com.example.m_commerce_admin.features.coupons.presentation.component.CouponCard
import com.example.m_commerce_admin.features.coupons.presentation.viewModel.CouponsViewModel
import com.example.m_commerce_admin.features.coupons.presentation.states.AddCouponState
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
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.fetchAllCoupons()
    }
    // Handle successful addition
    LaunchedEffect(addCouponState) {
        if (addCouponState is AddCouponState.Success) {
            viewModel.fetchAllCoupons() // 🔁 Refresh list
            scope.launch {
                snackbarHostState.showSnackbar("Coupon added Successfully!")
            }
            viewModel.resetAddCouponState()
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
                        coupon = CouponItem(
                            code = coupon.code ?: "N/A",
                            value = coupon.value ?: 0.0,
                            usedCount = coupon.usedCount ?: 0,
                            startsAt = coupon.startsAt ?: "N/A",
                            endsAt = coupon.endsAt ?: "N/A",
                            title = coupon.title,
                            summary = coupon.summary,
                            usageLimit = coupon.usageLimit,
                            createdAt = coupon.createdAt ?: "N/A",
                            updatedAt = coupon.updatedAt ?: "N/A",
                            amount = coupon.amount ?: 0.0,
                            currencyCode = coupon.currencyCode ?: "USD"
                        )
                    )
                }
            }
        }
    }
}
