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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CouponCard(
    coupon: CouponItem,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val status = getCouponStatus(coupon)
    val statusColor = when (status) {
        "Active" -> LightGreen
        "Expired" -> lightRed
        "Not Started" -> Teal
        else -> DarkGray
    }

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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Code: ${coupon.code}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = DarkestGray
                    )
                    
                    // Status badge
                    Text(
                        text = status,
                        fontSize = 12.sp,
                        color = White,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .background(color = statusColor, shape = RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
                
                Row {
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Teal)
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = lightRed)
                    }
                }
            }

            Spacer(modifier = Modifier.padding(8.dp))

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

            Spacer(modifier = Modifier.padding(8.dp))

            // Discount information
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (coupon.value != null) {
                            "Discount: ${coupon.value?.times(100)?.toInt()}%"
                        } else {
                            "Fixed Amount: ${coupon.amount} ${coupon.currencyCode}"
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Teal
                    )
                    
                    // Usage information
                    Text(
                        text = "Used: ${coupon.usedCount ?: 0}${coupon.usageLimit?.let { " / $it" } ?: ""}",
                        fontSize = 12.sp,
                        color = DarkGray
                    )
                }
            }

            Spacer(modifier = Modifier.padding(8.dp))

            // Date information
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                coupon.startsAt?.let { startDate ->
                    if (startDate != "null" && startDate.isNotEmpty()) {
                        Text(
                            text = "From: ${formatIsoDate(startDate)}",
                            fontSize = 12.sp,
                            color = DarkGray
                        )
                    }
                }
                
                coupon.endsAt?.let { endDate ->
                    if (endDate != "null" && endDate.isNotEmpty()) {
                        Text(
                            text = "To: ${formatIsoDate(endDate)}",
                            fontSize = 12.sp,
                            color = DarkGray
                        )
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun getCouponStatus(coupon: CouponItem): String {
    val now = LocalDateTime.now()
    val formatter = DateTimeFormatter.ISO_DATE_TIME
    
    val startsAt = coupon.startsAt?.let { 
        try { LocalDateTime.parse(it, formatter) } catch (e: Exception) { null }
    }
    val endsAt = coupon.endsAt?.let { 
        try { LocalDateTime.parse(it, formatter) } catch (e: Exception) { null }
    }
    
    return when {
        startsAt != null && now.isBefore(startsAt) -> "Not Started"
        endsAt != null && now.isAfter(endsAt) -> "Expired"
        else -> "Active"
    }
}


