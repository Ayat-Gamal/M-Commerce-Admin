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
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.m_commerce_admin.features.coupons.presentation.component.CouponCard
import com.example.m_commerce_admin.features.coupons.presentation.CouponsViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.m_commerce_admin.features.coupons.domain.entity.CouponItem

@Composable
fun CouponScreenUI(modifier: Modifier = Modifier, viewModel: CouponsViewModel = hiltViewModel()) {
    val coupons = viewModel.coupons.collectAsState().value
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight() ,
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
                //TODO( Search)
                }
            }
            items(coupons) { coupon ->
                CouponCard(
                    CouponItem(
                        code = coupon.code,
                        value = coupon.value!!,
                        usedCount = coupon.usedCount,
                        startsAt = coupon.startsAt!!,
                        endsAt = coupon.endsAt,
                        title = coupon.title,
                        summary = coupon.summary,
                        usageLimit = coupon.usageLimit,
                        createdAt = coupon.createdAt,
                        updatedAt = coupon.updatedAt,
                        amount = coupon.amount,
                        currencyCode = coupon.currencyCode
                    )
                )
            }
        }
    }
}