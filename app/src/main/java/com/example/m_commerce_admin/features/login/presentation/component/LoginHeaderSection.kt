package com.example.m_commerce_admin.features.login.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginHeaderSection() {
    Text("Admin Log In", fontSize = 32.sp, fontWeight = FontWeight.Medium, color = Color.Black)
    Spacer(Modifier.height(14.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {

        Text("Welcome, ", color = Color.DarkGray, fontSize = 14.sp)
    }
    Text("Please Login to admin dashboard", color = Color.DarkGray, fontSize = 14.sp)
    Spacer(Modifier.height(32.dp))
}