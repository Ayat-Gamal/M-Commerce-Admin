package com.example.m_commerce_admin.features.home.presentation.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.example.m_commerce_admin.config.theme.LightTeal
import com.example.m_commerce_admin.config.theme.Teal
import com.example.m_commerce_admin.config.theme.White
import com.example.m_commerce_admin.core.shared.components.SvgImage
 /*
@Composable
fun HomeCard() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = LightTeal
        ),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = CardDefaults.elevatedShape,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .border(
                border = BorderStroke(width = 1.dp, color = Teal),
                shape = RoundedCornerShape(16.dp)
            )
            .background(color = White)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                CardTitle(data.title)
                SvgImage(
                    data.icon,
                    modifier = Modifier.size(24.dp),
                    contentDescription = data.description,
                    colorFilter = ColorFilter.tint(Teal),
                )
            }
            CardDetails(data.value, data.description)
        }
    }
}*/