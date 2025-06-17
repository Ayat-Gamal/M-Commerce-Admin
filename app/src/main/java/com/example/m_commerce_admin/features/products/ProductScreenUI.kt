package com.example.m_commerce_admin.features.products

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.m_commerce_admin.R
import com.example.m_commerce_admin.config.theme.LightGreen
import com.example.m_commerce_admin.config.theme.LightTeal
import com.example.m_commerce_admin.config.theme.Teal
import com.example.m_commerce_admin.config.theme.White
import com.example.m_commerce_admin.config.theme.lightRed
import com.example.m_commerce_admin.core.shared.components.CustomButton
import com.example.m_commerce_admin.core.shared.components.CustomHeader
import com.example.m_commerce_admin.core.shared.components.SvgImage

data class ProductObject(
    val title: String,
    val image: String,
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
                           .padding(horizontal = 16.dp, vertical = 2.dp),)

                    CustomButton(
                        text = "Add Product",
                        height = 36,
                        backgroundColor = Teal,
                        textColor = Color.White, // Now explicitly white
                        onClick = { Log.i("addProduct", "clicked") }
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
            item { ProductCard(ProductObject("T-Shirt", "...", 120.0, 20240610, "Inactive", 20)) }
            item { ProductCard(ProductObject("T-Shirt", "...", 120.0, 20240610, "Active", 20)) }
            item { ProductCard(ProductObject("T-Shirt", "...", 120.0, 20240610, "Inactive", 20)) }
            item { ProductCard(ProductObject("T-Shirt", "...", 120.0, 20240610, "Active", 20)) }
            item { ProductCard(ProductObject("T-Shirt", "...", 120.0, 20240610, "Active", 20)) }
        }


    }
}

@Composable
fun ProductCard(product: ProductObject) {
    Card(
        colors = CardDefaults.cardColors(containerColor = LightTeal),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .border(BorderStroke(1.dp, Teal), RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SvgImage(
                R.drawable.ic_launcher_foreground,
                modifier = Modifier.size(80.dp),
                contentDescription = product.title,
                colorFilter = ColorFilter.tint(Teal),
            )

            Spacer(modifier = Modifier.size(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(product.title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Text("EGP ${product.price}", fontSize = 14.sp, color = Color.DarkGray)
                Text("In Stock: ${product.quantity}", fontSize = 12.sp)



                val statusColor = if (product.status == "Active") LightGreen else lightRed
                Text(
                    product.status,
                    fontSize = 12.sp,
                    color = Color.White,
                    modifier = Modifier
                        .background(statusColor, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }

            Column  {
                IconButton(onClick = {  }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Teal)
                }
                IconButton(onClick = {   }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                }
            }
        }
    }

}



@Preview(showBackground = true)
@Composable
fun PreviewProductCard() {
    ProductCard(ProductObject("Sneakers", "...", 199.99, 20240616, "Active", 12))
}
