package com.example.m_commerce_admin.core.shared.components
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.m_commerce_admin.config.theme.LightGray
import com.example.m_commerce_admin.config.theme.Teal
import com.example.m_commerce_admin.config.theme.White

@Composable
fun SmallButton(
    modifier: Modifier = Modifier,
    label: String,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth() // ensure same width
            .height(40.dp)  // same height as standard button
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, color = Teal, shape = RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .background(White) // gives contrast against filled button
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = label,
            style = TextStyle(color = Teal, fontWeight = FontWeight.Bold)
        )
    }
}
