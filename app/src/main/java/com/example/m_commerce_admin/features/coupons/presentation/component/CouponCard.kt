package com.example.m_commerce_admin.features.coupons.presentation.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.m_commerce_admin.config.theme.DarkestGray
import com.example.m_commerce_admin.config.theme.LightGreen
import com.example.m_commerce_admin.config.theme.Teal
import com.example.m_commerce_admin.config.theme.White
import com.example.m_commerce_admin.config.theme.lightRed
import com.example.m_commerce_admin.core.helpers.formatIsoDate
import com.example.m_commerce_admin.features.coupons.domain.entity.CouponItem

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CouponCard(
    coupon: CouponItem,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .background(color = White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Code: ${coupon.code}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = DarkestGray
                )
                Row {
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Teal)
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = lightRed)
                    }
                }
            }

            Spacer(modifier = Modifier.padding(4.dp))

            coupon.title?.let {
                Text(
                    text = it,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = DarkGray
                )
            }

            coupon.summary?.let {
                Text(
                    text = it,
                    fontSize = 13.sp,
                    color = DarkGray,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Spacer(modifier = Modifier.padding(4.dp))

            Text(
                text =
                if (coupon.value == null) "Fixed Amount Coupon" else "Discount: ${
                    coupon.value?.times(
                        100
                    )?.toInt()
                }%",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = DarkGray
            )

            Text(
                text = if (coupon.amount.equals(0.0)) "Percentage Coupon" else "${coupon.amount} ${coupon.currencyCode}",
                fontSize = 14.sp,
                color = DarkGray
            )

            Text(
                text = "Start: ${formatIsoDate(coupon.startsAt ?: "00:00:00")}",
                fontSize = 13.sp,
                color = White,
                modifier = Modifier
                    .padding(top = 6.dp)
                    .background(color = LightGreen, shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}


