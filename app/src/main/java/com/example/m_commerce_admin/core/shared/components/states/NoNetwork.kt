package com.example.m_commerce_admin.core.shared.components.states

import com.example.m_commerce_admin.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun NoNetwork() {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimateLottie(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            resourceId = R.raw.offline
        )
        Text(
            text = "No Internet connection",
            modifier = Modifier.padding(horizontal = 24.dp),
            fontSize = 18.sp,
            color = Color(0xFF808080),
            textAlign = TextAlign.Center
        )
    }
}