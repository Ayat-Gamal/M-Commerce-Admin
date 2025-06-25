package com.example.m_commerce_admin.features.coupons.presentation.component

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.m_commerce_admin.config.theme.Teal
import com.example.m_commerce_admin.features.coupons.domain.entity.CouponInput
import com.example.m_commerce_admin.features.coupons.domain.entity.CouponItem
import com.example.m_commerce_admin.features.coupons.domain.entity.DiscountType
import com.example.m_commerce_admin.features.coupons.domain.entity.DiscountType.*
import com.example.m_commerce_admin.features.coupons.presentation.viewModel.CouponsViewModel
import com.example.m_commerce_admin.features.coupons.presentation.states.AddCouponState
import com.example.m_commerce_admin.features.coupons.presentation.states.UpdateCouponState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import androidx.compose.material3.SnackbarResult
import androidx.compose.ui.draw.alpha
import androidx.compose.foundation.background

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCouponFormUI(
    viewModel: CouponsViewModel = hiltViewModel(),
    navController: NavController? = null,
    couponId: String? = null // if not null → we're editing

) {
    val coupons = viewModel.coupons.collectAsState().value
    val editCoupon = remember(couponId, coupons) {
        coupons.find { it.id == couponId }
    }
    val titleState = remember { mutableStateOf("") }
    val summaryState = remember { mutableStateOf("") }
    val codeState = remember { mutableStateOf("") }
    val discountValueState = remember { mutableStateOf("") }
    val usageLimitState = remember { mutableStateOf("") }
    val selectedDiscountType = remember { mutableStateOf(PERCENTAGE) }

    // Initialize form with editCoupon data
    LaunchedEffect(editCoupon) {
        editCoupon?.let { coupon ->
            titleState.value = coupon.title.orEmpty()
            summaryState.value = coupon.summary.orEmpty()
            codeState.value = coupon.code
            discountValueState.value = coupon.value?.toString() ?: coupon.amount.toString()
            usageLimitState.value = coupon.usageLimit?.toString().orEmpty()
            selectedDiscountType.value = if (coupon.value != null) PERCENTAGE else FIXED_AMOUNT
        }
    }
    

    val isFormValid = remember(titleState.value, codeState.value, discountValueState.value) {
        val discount = discountValueState.value.toDoubleOrNull()
        titleState.value.isNotBlank() && codeState.value.isNotBlank() && (discount != null && discount > 0)
    }

    val state by viewModel.addCouponState.collectAsState()
    val updateState by viewModel.updateCouponState.collectAsState()
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Determine current state based on edit mode
    val currentState = if (editCoupon == null) state else updateState
    val isLoading = currentState is AddCouponState.Loading || currentState is UpdateCouponState.Loading
    val isSuccess = currentState is AddCouponState.Success || currentState is UpdateCouponState.Success
    val isError = currentState is AddCouponState.Error || currentState is UpdateCouponState.Error
    val errorMessage = when (currentState) {
        is AddCouponState.Error -> currentState.message
        is UpdateCouponState.Error -> currentState.message
        else -> null
    }

    // Fetch coupons if we're editing and the list is empty
    LaunchedEffect(couponId) {
        if (couponId != null && coupons.isEmpty()) {
            viewModel.fetchAllCoupons()
        }
    }

    // Wait for coupons to be loaded when editing
    val isEditMode = couponId != null
    val isReadyForEdit = !isEditMode || (isEditMode && editCoupon != null)

    // Add timeout mechanism to prevent stuck loading state
    LaunchedEffect(currentState) {
        android.util.Log.d("TAG", "AddCouponFormUI: Current state changed to: $currentState")
        
        when (currentState) {
            is AddCouponState.Loading, is UpdateCouponState.Loading -> {
                android.util.Log.d("TAG", "AddCouponFormUI: Loading state")
                // Don't block here - just log the loading state
            }
            
            is AddCouponState.Success -> {
                android.util.Log.d("TAG", "AddCouponFormUI: Add success state, showing snackbar and resetting")
                scope.launch {
                    snackbarHostState.showSnackbar("Coupon added successfully")
                    delay(1500)
                    viewModel.resetAddCouponState()
                    navController?.popBackStack()
                }
            }

            is UpdateCouponState.Success -> {
                android.util.Log.d("TAG", "AddCouponFormUI: Update success state, showing snackbar and resetting")
                scope.launch {
                    snackbarHostState.showSnackbar("Coupon updated successfully")
                    delay(1500)
                    viewModel.resetUpdateCouponState()
                    navController?.popBackStack()
                }
            }

            is AddCouponState.Error -> {
                android.util.Log.e("TAG", "AddCouponFormUI: Add error state: ${currentState.message}")
                scope.launch {
                    val result = snackbarHostState.showSnackbar(
                        message = currentState.message,
                        actionLabel = "Retry"
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.retryLastOperation()
                    } else {
                        viewModel.resetAddCouponState()
                    }
                }
            }

            is UpdateCouponState.Error -> {
                android.util.Log.e("TAG", "AddCouponFormUI: Update error state: ${currentState.message}")
                scope.launch {
                    val result = snackbarHostState.showSnackbar(
                        message = currentState.message,
                        actionLabel = "Retry"
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.retryLastOperation()
                    } else {
                        viewModel.resetUpdateCouponState()
                    }
                }
            }

            is AddCouponState.Idle, is UpdateCouponState.Idle -> {
                android.util.Log.d("TAG", "AddCouponFormUI: Idle state")
            }
        }
    }

    // Separate timeout mechanism that doesn't block state transitions
    LaunchedEffect(currentState) {
        if (currentState is AddCouponState.Loading || currentState is UpdateCouponState.Loading) {
            android.util.Log.d("TAG", "AddCouponFormUI: Starting timeout for loading state")
            try {
                delay(30000) // 30 second timeout (was incorrectly set to 3000ms)
                // Check if still in loading state after timeout
                val currentAddState = viewModel.addCouponState.value
                val currentUpdateState = viewModel.updateCouponState.value
                if (currentAddState is AddCouponState.Loading || currentUpdateState is UpdateCouponState.Loading) {
                    android.util.Log.w("TAG", "AddCouponFormUI: Loading timeout reached, resetting states")
                    viewModel.resetAddCouponState()
                    viewModel.resetUpdateCouponState()
                    scope.launch {
                        snackbarHostState.showSnackbar("Operation timed out. Please check your internet connection and try again.")
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("TAG", "AddCouponFormUI: Timeout error", e)
                // Reset states on timeout error as well
                viewModel.resetAddCouponState()
                viewModel.resetUpdateCouponState()
                scope.launch {
                    snackbarHostState.showSnackbar("Operation failed. Please try again.")
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (editCoupon == null) "Add New Coupon" else "Edit Coupon",
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
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Show loading indicator if we're editing but coupon data isn't ready
                if (isEditMode && !isReadyForEdit) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    FormField(
                        value = titleState.value,
                        onValueChange = { titleState.value = it },
                        label = "Coupon Title",
                        placeholder = "Enter coupon title",
                        isError = titleState.value.isBlank() && !isFormValid
                    )

                    FormField(
                        value = summaryState.value,
                        onValueChange = { summaryState.value = it },
                        label = "Description (Optional)",
                        placeholder = "Enter coupon description",
                        isError = false,
                        singleLine = false
                    )

                    FormField(
                        value = codeState.value,
                        onValueChange = {  codeState.value = it.uppercase() },
                        label = "Coupon Code",
                        placeholder = "e.g., SAVE20",
                        isError =  codeState.value.isBlank() && !isFormValid
                    )

                    FormField(
                        value = discountValueState.value,
                        onValueChange = {
                            if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) discountValueState.value = it
                        },
                        label = if (selectedDiscountType.value == PERCENTAGE) "Discount Percentage" else "Discount Amount",
                        placeholder = if (selectedDiscountType.value == PERCENTAGE) "20.0" else "10.00",
                        isError = discountValueState.value.isBlank() || discountValueState.value.toDoubleOrNull()
                            ?.let { it <= 0 } ?: true,
                        keyboardType = KeyboardType.Decimal
                    )

                    FormField(
                        value = usageLimitState.value,
                        onValueChange = {
                            if (it.isEmpty() || it.matches(Regex("^\\d*$"))) usageLimitState.value = it
                        },
                        label = "Usage Limit (Optional)",
                        placeholder = "e.g., 100",
                        isError = false,
                        keyboardType = KeyboardType.Number
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(Color(0xFFF8F9FA))
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                "Discount Type",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                DiscountType.entries.forEach { discountType ->
                                    FilterChip(
                                        onClick = { selectedDiscountType.value = discountType },
                                        label = { Text(discountType.name.replace("_", " ")) },
                                        selected = selectedDiscountType.value == discountType,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    Button(
                        onClick = {
                            // Check if form is valid before proceeding
                            if (!isFormValid) {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Please fill in all required fields correctly.")
                                }
                                return@Button
                            }
                            
                            val input = CouponInput(
                                id = editCoupon?.id, // include ID for update
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

                            if (editCoupon == null) {
                                viewModel.addCoupon(input)
                            } else {
                                viewModel.updateCoupon(input)
                            }
                        },
                        enabled = isFormValid && !isLoading && (editCoupon == null || isReadyForEdit),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        if (isLoading) {
                         Text("jjjj")
                        } else {
                            Text(if (editCoupon == null) "Add Coupon" else "Update Coupon")
                        }
                    }
                }
            }
            
            // Loading overlay
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier.padding(16.dp),
                        colors = CardDefaults.cardColors(Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.padding(16.dp))
                            Text(
                                text = if (editCoupon == null) "Creating coupon..." else "Updating coupon...",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

