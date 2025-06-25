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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.m_commerce_admin.config.theme.Teal
import com.example.m_commerce_admin.features.coupons.domain.entity.CouponInput
import com.example.m_commerce_admin.features.coupons.domain.entity.DiscountType.*
import com.example.m_commerce_admin.features.coupons.presentation.viewModel.CouponsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import androidx.compose.material3.SnackbarResult
import com.example.m_commerce_admin.features.coupons.presentation.states.CouponFormState


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddForm(
    viewModel: CouponsViewModel = hiltViewModel(),
    navController: NavController? = null,
) {
    val state by viewModel.couponFormState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()


    // UI States
    val titleState = remember { mutableStateOf("") }
    val summaryState = remember { mutableStateOf("") }
    val codeState = remember { mutableStateOf("") }
    val discountValueState = remember { mutableStateOf("") }
    val usageLimitState = remember { mutableStateOf("") }
    val selectedDiscountType = remember { mutableStateOf(PERCENTAGE) }

    // Validation
    val isFormValid = remember(titleState.value, codeState.value, discountValueState.value) {
        val discount = discountValueState.value.toDoubleOrNull()
        titleState.value.isNotBlank() && codeState.value.isNotBlank() && discount != null && discount > 0
    }


    // Handle state changes (success, error, timeout)
    LaunchedEffect(state) {
        when (state) {
            is CouponFormState.Success -> {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        "Coupon added successfully"
                    )
                    delay(1500)
                    viewModel.resetCouponFormState()
                    navController?.popBackStack()
                }
            }

            is CouponFormState.Error -> {
                val message = (state as CouponFormState.Error).message
                scope.launch {
                    val result = snackbarHostState.showSnackbar(
                        message = message,
                        actionLabel = "Retry"
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.retryLastOperation()
                    } else {
                        viewModel.resetCouponFormState()
                    }
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
                        "Add New Coupon",
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


                CouponForm(
                    title = titleState,
                    summary = summaryState,
                    code = codeState,
                    discountValue = discountValueState,
                    usageLimit = usageLimitState,
                    selectedDiscountType = selectedDiscountType,
                    isFormValid = isFormValid,

                    ) {
                    val input = CouponInput(
                        title = titleState.value,
                        summary = summaryState.value.takeIf { it.isNotBlank() },
                        code = codeState.value,
                        startsAt = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                        endsAt = null,
                        usageLimit = usageLimitState.value.toIntOrNull(),
                        discountType = selectedDiscountType.value,
                        discountValue = discountValueState.value.toDoubleOrNull() ?: 0.0,
                    )

                    viewModel.addCoupon(input)

                }
            }


        }


    }


}

