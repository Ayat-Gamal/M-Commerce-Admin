package com.example.m_commerce_admin.features.coupons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.m_commerce_admin.features.coupons.component.Coupon
import com.example.m_commerce_admin.features.coupons.component.CouponCard


@Composable
fun CouponScreenUI(modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier
            .fillMaxSize()

    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            item {
                Spacer(modifier = modifier.padding(top = 1.dp))
                Row(
                    modifier = Modifier
                        .padding(all = 16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {

                }

            }
            item {
                CouponCard(
                    Coupon(code = "SUMMER25", value = 20.0, usedCount = 5, startsAt = "20250520", endsAt = "20250622")
                )
            }
            item {
                CouponCard(
                    Coupon(code = "SUMMER25", value = 20.0, usedCount = 5, startsAt = "20250520", endsAt = "20250622")
                )
            }
            item {
                CouponCard(
                    Coupon(code = "SUMMER25", value = 20.0, usedCount = 5, startsAt = "20250520", endsAt = "20250622")
                )
            }
            item {
                CouponCard(
                    Coupon(code = "SUMMER25", value = 20.0, usedCount = 5, startsAt = "20250520", endsAt = "20250622")
                )
            }
            item {
                CouponCard(
                    Coupon(code = "SUMMER25", value = 20.0, usedCount = 5, startsAt = "20250520", endsAt = "20250622")
                )
            }
            item {
                CouponCard(
                    Coupon(code = "SUMMER25", value = 20.0, usedCount = 5, startsAt = "20250520", endsAt = "20250622")
                )
            }

        }


    }
}