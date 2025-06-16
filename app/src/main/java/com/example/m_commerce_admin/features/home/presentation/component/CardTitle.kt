package com.example.m_commerce_admin.features.home.presentation.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.m_commerce_admin.config.theme.Black

@Composable
fun CardTitle(title: String) {
    Text(
        text = title, style = TextStyle(
            color = Black,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
        )
    )
}
