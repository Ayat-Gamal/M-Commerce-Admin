package com.example.m_commerce_admin.features.coupons.presentation.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.m_commerce_admin.config.theme.LightTeal
import com.example.m_commerce_admin.config.theme.Teal
import com.example.m_commerce_admin.features.coupons.domain.entity.CouponInput
import com.example.m_commerce_admin.features.coupons.domain.entity.DiscountType.FIXED_AMOUNT
import com.example.m_commerce_admin.features.coupons.domain.entity.DiscountType.PERCENTAGE
import com.example.m_commerce_admin.features.coupons.presentation.states.CouponFormState
import com.example.m_commerce_admin.features.coupons.presentation.viewModel.CouponsViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCouponFormUI(
    viewModel: CouponsViewModel = hiltViewModel(),
    navController: NavController? = null,
    couponId: String? = null
) {
    val coupons = viewModel.coupons.collectAsState().value
    val state by viewModel.couponFormState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val editCoupon = remember(couponId, coupons) {
        coupons.find { it.id == couponId }
    }

    // UI States
    val titleState = remember { mutableStateOf("") }
    val summaryState = remember { mutableStateOf("") }
    val codeState = remember { mutableStateOf("") }
    val discountValueState = remember { mutableStateOf("") }
    val usageLimitState = remember { mutableStateOf("") }
    val selectedDiscountType = remember { mutableStateOf(PERCENTAGE) }

    LaunchedEffect(editCoupon) {

        editCoupon?.let {
            titleState.value = it.title.orEmpty()
            summaryState.value = it.summary.orEmpty()
            codeState.value = it.code
            discountValueState.value = it.value?.toString() ?: it.amount.toString()
            usageLimitState.value = it.usageLimit?.toString().orEmpty()
            selectedDiscountType.value = if (it.value != null) PERCENTAGE else FIXED_AMOUNT
        }
    }

    val isFormValid = remember(titleState.value, codeState.value, discountValueState.value) {
        val discount = discountValueState.value.toDoubleOrNull()
        titleState.value.isNotBlank() && codeState.value.isNotBlank() && discount != null && discount > 0
    }

    LaunchedEffect(couponId) {
        if (couponId != null && coupons.isEmpty()) {
            viewModel.fetchAllCoupons()
        }
    }

    LaunchedEffect(state) {
        when (state) {
            is CouponFormState.Success -> {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        "Coupon updated successfully"
                    )
                    navController?.popBackStack()
                    viewModel.resetCouponFormState()

                }
            }

            is CouponFormState.Error -> {
                val message = (state as CouponFormState.Error).message
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = message,
                    )
                    viewModel.resetCouponFormState()

                }
            }

            else -> Unit
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Edit Coupon",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Teal,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState) { Snackbar(it) }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                 val isEditMode = couponId != null
                val isEditLoading = isEditMode && editCoupon == null

                if (isEditLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = LightTeal)
                    }
                } else {
                    CouponForm(
                        title = titleState,
                        summary = summaryState,
                        code = codeState,
                        discountValue = discountValueState,
                        usageLimit = usageLimitState,
                        selectedDiscountType = selectedDiscountType,
                        isFormValid = isFormValid,
                        isEditMode = isEditMode
                    ) {
                        val input = CouponInput(
                            id = editCoupon?.id,
                            title = titleState.value,
                            summary = summaryState.value.takeIf { it.isNotBlank() },
                            code = codeState.value,
                            startsAt = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                            endsAt = null,
                            usageLimit = usageLimitState.value.toIntOrNull(),
                            discountType = selectedDiscountType.value,
                            discountValue = discountValueState.value.toDoubleOrNull() ?: 0.0,
                            currencyCode = editCoupon?.currencyCode ?: "USD"
                        )
                        viewModel.updateCoupon(input)
                    }
                }
            }
        }
    }
}
