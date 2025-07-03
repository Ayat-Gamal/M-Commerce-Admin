package com.example.m_commerce_admin.core.shared.components


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun CustomOutlinedTextField(
    modifier: Modifier = Modifier,
    state: MutableState<String>,
    hint: String,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    isLoading: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    return OutlinedTextField(
        state.value,
        {
            if (!isLoading) {
                state.value = it
            }
        },
        placeholder = { Text(hint, color = Color(0xFFC4C4C4)) },
        modifier = modifier
            .fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = visualTransformation,

        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color(0xFFEEEEEE),
            focusedContainerColor = Color(0xFFEEEEEE),
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = Color.Transparent,
        ),
        shape = RoundedCornerShape(16.dp),
        maxLines = 1,
        trailingIcon = trailingIcon,
    )
}