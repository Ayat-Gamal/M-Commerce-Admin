package com.example.m_commerce_admin.features.home.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.m_commerce_admin.config.theme.Gray
import com.example.m_commerce_admin.config.theme.Teal
import com.example.m_commerce_admin.config.theme.White


@Composable
fun QuickAccessButtons(
    onAddProductClick: () -> Unit,
    onAddCouponClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onAddProductClick,

                modifier = Modifier.padding(8.dp), colors = ButtonColors(
                    contentColor = White,
                    containerColor = Teal,
                    disabledContainerColor = Gray,
                    disabledContentColor = White
                )
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Product", style = MaterialTheme.typography.bodyMedium)
            }

            Button(
                onClick = onAddCouponClick,
                modifier = Modifier.padding(8.dp), colors = ButtonColors(
                    contentColor = White,
                    containerColor = Teal,
                    disabledContainerColor = Gray,
                    disabledContentColor = White
                )


            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Coupon", style = MaterialTheme.typography.bodyMedium)
            }
        }

    }
}
