package com.example.m_commerce_admin.features.home.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.m_commerce_admin.config.theme.DarkestGray
import com.example.m_commerce_admin.config.theme.Gray


@Composable
fun CardDetails(value: Int?, description: String) {
    Column {
        Spacer(Modifier.height(12.dp))
        Text(
            text = value.toString(), style = TextStyle(
                color = DarkestGray,
                fontSize = 24.sp,
                fontWeight = FontWeight.Normal,
            )
        )
        Spacer(Modifier.height(12.dp))

        Text(
            text = description, style = TextStyle(
                color = Gray,
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
            )
        )

    }
}

