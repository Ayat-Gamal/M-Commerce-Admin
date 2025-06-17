package com.example.m_commerce_admin.features.products

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import com.example.m_commerce_admin.config.theme.White
import com.example.m_commerce_admin.core.shared.components.CustomButton
import com.example.m_commerce_admin.core.shared.components.CustomHeader
import com.example.m_commerce_admin.features.products.component.ProductCard

data class ProductObject(
    val title: String,
    val image: Int,
    val price: Double,
    val createdAt: Int,
    val status: String,
    val quantity: Int
)


@Preview(showBackground = true)
@Composable
fun ProductScreenUI(modifier: Modifier = Modifier) {

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
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
                    CustomHeader(
                        "Products",
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 2.dp),
                    )

                    CustomButton(
                        text = "Add Product",
                        height = 36,
                        backgroundColor = Teal,
                        textColor = White,
                        onClick = { Log.i("addProduct", "clicked") })
                }
                HorizontalDivider(
                    color = Teal, thickness = 2.dp, modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 16.dp)
                )

            }
            item {
                ProductCard(
                    ProductObject(
                        "T-Shirt",
                        R.drawable.placeholder2,
                        820.0,
                        20240610,
                        "Inactive",
                        20
                    )
                )
            }
            item {
                ProductCard(
                    ProductObject(
                        "Sneakers",
                        R.drawable.shose1,
                        230.0,
                        20240125,
                        "Active",
                        40
                    )
                )
            }
            item {
                ProductCard(
                    ProductObject(
                        "Blouse",
                        R.drawable.tshitt1,
                        120.0,
                        20240825,
                        "Inactive",
                        14
                    )
                )
            }
            item {
                ProductCard(
                    ProductObject(
                        "T-Shirt",
                        R.drawable.placeholder2,
                        120.0,
                        20240614,
                        "Active",
                        33
                    )
                )
            }
            item {
                ProductCard(
                    ProductObject(
                        "T-Shirt",
                        R.drawable.tshitt1,
                        120.0,
                        20240610,
                        "Active",
                        20
                    )
                )
            }
        }


    }
}


@Preview(showBackground = true)
@Composable
fun PreviewProductCard() {
    ProductCard(ProductObject("Sneakers", R.drawable.shose1, 199.99, 20240616, "Active", 12))
}
