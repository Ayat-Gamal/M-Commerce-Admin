package com.example.m_commerce_admin.core.shared.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.m_commerce_admin.config.theme.White

@Composable
fun CustomButton(
    text: String,
    modifier: Modifier = Modifier,
    height: Int = 56,
    cornerRadius: Int = 24,
    backgroundColor: Color =
        LightGray,
    textColor: Color = White,
    onClick: () -> Unit

) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(height.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(cornerRadius.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            disabledContainerColor = backgroundColor.copy(alpha = 0.7f),

            contentColor = textColor
        )
    ) {
            Text(text = text, fontSize = 16.sp)
    }
}
