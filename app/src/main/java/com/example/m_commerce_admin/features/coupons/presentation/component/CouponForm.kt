package com.example.m_commerce_admin.features.coupons.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.m_commerce_admin.config.theme.LightTeal
import com.example.m_commerce_admin.config.theme.Teal
import com.example.m_commerce_admin.config.theme.White
import com.example.m_commerce_admin.features.coupons.domain.entity.DiscountType
import com.example.m_commerce_admin.features.coupons.domain.entity.DiscountType.PERCENTAGE
import com.example.m_commerce_admin.features.coupons.domain.entity.DiscountType.entries
import com.example.m_commerce_admin.features.coupons.presentation.states.CouponFormState
import kotlinx.coroutines.launch


@Composable
fun CouponForm(
    title: MutableState<String>,
    summary: MutableState<String>,
    code: MutableState<String>,
    discountValue: MutableState<String>,
    usageLimit: MutableState<String>,
    selectedDiscountType: MutableState<DiscountType>,
    isFormValid: Boolean,
    isEditMode: Boolean = false,
    state: CouponFormState,
    onSubmit: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val isLoading =
        state is CouponFormState.Loading
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FormField(
            value = title.value,
            onValueChange = { title.value = it },
            label = "Coupon Title",
            placeholder = "Enter coupon title",
            isError = title.value.isBlank() && !isFormValid
        )

        FormField(
            value = summary.value,
            onValueChange = { summary.value = it },
            label = "Description (Optional)",
            placeholder = "Enter coupon description",
            isError = false,
            singleLine = false
        )

        FormField(

            isEnabled = if (isEditMode) false else true,
            value = code.value,
            onValueChange = { code.value = it.uppercase() },
            label = "Coupon Code",
            placeholder = "e.g., SAVE20",
            isError = code.value.isBlank() && !isFormValid,

            )

        FormField(
            value = discountValue.value,
            onValueChange = {
                if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) discountValue.value = it
            },
            label = if (selectedDiscountType.value == PERCENTAGE) "Discount Percentage" else "Discount Amount",
            placeholder = if (selectedDiscountType.value == PERCENTAGE) "20.0" else "10.00",
            isError = discountValue.value.isBlank() || discountValue.value.toDoubleOrNull()
                ?.let { it <= 0 } ?: true,
            keyboardType = KeyboardType.Decimal
        )

        FormField(
            value = usageLimit.value,
            onValueChange = {
                if (it.isEmpty() || it.matches(Regex("^\\d*$"))) usageLimit.value = it
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
                    entries.forEach { discountType ->
                        FilterChip(

                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = LightTeal,
                                selectedLabelColor = Color.White
                            ), onClick = { selectedDiscountType.value = discountType },
                            label = { Text(discountType.name.replace("_", " ")) },
                            selected = selectedDiscountType.value == discountType,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        Button(
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Teal,
                disabledContainerColor = LightGray.copy(alpha = 0.7f),
                contentColor = White
            ),
            onClick = {
                if (!isFormValid) {
                    scope.launch {
                        snackbarHostState.showSnackbar("Please fill in all required fields correctly.")
                    }
                    return@Button
                }
                onSubmit()
            },
            enabled = isFormValid || !isEditMode,
            modifier = Modifier
                .height(height = 50.dp)
                .fillMaxWidth(),

            ) {

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White
                )
            } else {


                Text(
                    text = if (isEditMode) "Update Coupon" else "Add Coupon",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
