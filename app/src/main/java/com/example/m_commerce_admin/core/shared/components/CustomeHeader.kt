package com.example.m_commerce_admin.core.shared.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.m_commerce_admin.config.theme.Black

@Composable
fun CustomHeader(text: String, modifier: Modifier = Modifier) {
    return Text(
        text,
        fontSize = 22.sp,
        fontWeight = FontWeight.SemiBold,
        color = Black,
        modifier = modifier
    )
}