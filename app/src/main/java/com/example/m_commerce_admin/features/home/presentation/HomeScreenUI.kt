package com.example.m_commerce_admin.features.home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.m_commerce_admin.R
import com.example.m_commerce_admin.config.theme.Teal
import com.example.m_commerce_admin.core.shared.components.CustomHeader
import com.example.m_commerce_admin.features.home.presentation.component.HomeCard

data class CardObject(val title: String, val value: Int, val icon: Int, val description: String)


@Preview(showBackground = true)
@Composable
fun HomeScreenUI() {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxHeight()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),

            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,

                    ) {
                    CustomHeader(
                        "Dashboard",
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 2.dp),

                        )
                }
                HorizontalDivider(
                    color = Teal,
                    thickness = 2.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 16.dp)
                )


            }

            item {
                HomeCard(
                    data = CardObject(
                        title = "Products",
                        value = 8,
                        icon = R.raw.products,
                        description = "Total Number of Products"
                    )
                )
            }
            item {
                HomeCard(
                    data = CardObject(
                        title = "Inventory",
                        value = 45,
                        icon = R.raw.inventory,
                        description = "Available in the stock"
                    )
                )
            }
            item {
                HomeCard(
                    data = CardObject(
                        title = "Active Coupons",
                        value = 3,
                        icon = R.raw.coupon,
                        description = "2 are scheduled"
                    )
                )
            }
            item {
                HomeCard(
                    data = CardObject(
                        title = "Inventory",
                        value = 45,
                        icon = R.raw.inventory,
                        description = "Available in the stock"
                    )
                )
            }
            item {
                HomeCard(
                    data = CardObject(
                        title = "Active Coupons",
                        value = 3,
                        icon = R.raw.coupon,
                        description = "2 are scheduled"
                    )
                )
            }
        }

    }

}

