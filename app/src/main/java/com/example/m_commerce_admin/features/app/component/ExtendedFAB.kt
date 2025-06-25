package com.example.m_commerce_admin.features.app.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.m_commerce_admin.config.theme.Black
import com.example.m_commerce_admin.config.theme.LightTeal
import com.example.m_commerce_admin.config.theme.Teal
import com.example.m_commerce_admin.config.theme.White

@Composable
fun FAB(onClick: () -> Unit, screen: String) {
    ExtendedFloatingActionButton(
        modifier = Modifier
            .padding(bottom = 0.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                color = LightTeal,
                shape = RoundedCornerShape(16.dp)
            ),
        onClick = onClick,
        icon = { Icon(Icons.Default.Add, screen, tint = Teal) },
        text = { Text(text = screen, style = TextStyle(color = Black)) },
        containerColor = White,
        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()

    )
}
