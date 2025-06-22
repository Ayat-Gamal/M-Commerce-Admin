package com.example.m_commerce_admin.core.shared.components
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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


@Composable
fun SmallButton(modifier: Modifier = Modifier, label: String, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(shape = RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .border(2.dp, color = LightGray, shape = RoundedCornerShape(20.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = label, style = TextStyle(color = Teal, fontWeight = FontWeight.W800))
    }
}