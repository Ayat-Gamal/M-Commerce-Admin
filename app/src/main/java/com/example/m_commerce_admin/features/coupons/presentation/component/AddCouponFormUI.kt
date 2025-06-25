package com.example.m_commerce_admin.features.coupons.presentation.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.example.m_commerce_admin.features.coupons.domain.entity.DiscountType
import com.example.m_commerce_admin.features.coupons.presentation.viewModel.CouponsViewModel
import com.example.m_commerce_admin.features.coupons.presentation.states.AddCouponState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCouponFormUI(
    viewModel: CouponsViewModel = hiltViewModel(),
    navController: NavController? = null
) {
    var title by remember { mutableStateOf("") }
    var summary by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var discountValue by remember { mutableStateOf("") }
    var usageLimit by remember { mutableStateOf("") }
    var selectedDiscountType by remember { mutableStateOf(DiscountType.PERCENTAGE) }
    var isFormValid by remember { mutableStateOf(false) }

    val state by viewModel.addCouponState.collectAsState()
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(title, code, discountValue) {
        val isDiscountValueValid = discountValue.toDoubleOrNull()?.let { it > 0 } ?: false
        isFormValid =
            title.isNotBlank() && code.isNotBlank() && discountValue.isNotBlank() && isDiscountValueValid
    }

    LaunchedEffect(state) {
        when (state) {
            is AddCouponState.Success -> {
                title = ""
                summary = ""
                code = ""
                discountValue = ""
                usageLimit = ""
                selectedDiscountType = DiscountType.PERCENTAGE
                delay(2000)

                viewModel.resetAddCouponState()
                navController?.popBackStack()
            }

            is AddCouponState.Error -> {
                scope.launch {
                    snackbarHostState.showSnackbar("Error: ${(state as AddCouponState.Error).message}")
                }
                viewModel.resetAddCouponState()
            }

            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Coupon", fontWeight = FontWeight.Bold) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FormField(
                value = title,
                onValueChange = { title = it },
                label = "Coupon Title",
                placeholder = "Enter coupon title",
                isError = title.isBlank() && !isFormValid
            )

            FormField(
                value = summary,
                onValueChange = { summary = it },
                label = "Description (Optional)",
                placeholder = "Enter coupon description",
                isError = false,
                singleLine = false
            )

            FormField(
                value = code,
                onValueChange = { code = it.uppercase() },
                label = "Coupon Code",
                placeholder = "e.g., SAVE20",
                isError = code.isBlank() && !isFormValid
            )

            FormField(
                value = discountValue,
                onValueChange = {
                    if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) discountValue = it
                },
                label = if (selectedDiscountType == DiscountType.PERCENTAGE) "Discount Percentage" else "Discount Amount",
                placeholder = if (selectedDiscountType == DiscountType.PERCENTAGE) "20.0" else "10.00",
                isError = discountValue.isBlank() || discountValue.toDoubleOrNull()
                    ?.let { it <= 0 } ?: true,
                keyboardType = KeyboardType.Decimal
            )

            FormField(
                value = usageLimit,
                onValueChange = {
                    if (it.isEmpty() || it.matches(Regex("^\\d*$"))) usageLimit = it
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
                                onClick = { selectedDiscountType = discountType },
                                label = { Text(discountType.name.replace("_", " ")) },
                                selected = selectedDiscountType == discountType,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            Button(
                onClick = {
                    val input = CouponInput(
                        title = title.trim(),
                        summary = summary.takeIf { it.isNotBlank() },
                        code = code.trim(),
                        startsAt = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                        endsAt = null,
                        usageLimit = usageLimit.toIntOrNull(),
                        discountType = selectedDiscountType,
                        discountValue = discountValue.toDoubleOrNull() ?: 0.0
                    )
                    scope.launch(Dispatchers.IO) {
                        viewModel.addCoupon(input)
                        scope.launch {
                            snackbarHostState.showSnackbar("Product added Successfully!")
                        }
                    }
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = isFormValid && state !is AddCouponState.Loading
            ) {
                if (state is AddCouponState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text("Add Coupon", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

