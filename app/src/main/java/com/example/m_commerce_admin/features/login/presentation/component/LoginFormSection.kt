package com.example.m_commerce_admin.features.login.presentation.component


import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.m_commerce_admin.config.theme.Teal
import com.example.m_commerce_admin.config.theme.White
import com.example.m_commerce_admin.core.shared.components.CustomButton
import com.example.m_commerce_admin.core.shared.components.CustomHeader
import com.example.m_commerce_admin.core.shared.components.CustomOutlinedTextField
import com.example.m_commerce_admin.features.login.presentation.viewModel.LoginViewModel

@Composable
fun LoginFormSection(
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    Column(modifier = Modifier.padding(8.dp)) {

        CustomHeader(
            "Username",
            Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        )
        CustomOutlinedTextField(state = username, hint = "i.e.John Doe",
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Toggle password visibility"
                )
            })

        Spacer(Modifier.height(16.dp))


        CustomHeader(
            "Password", Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)

        )

        AuthPasswordTextField(password, "**********")
        Spacer(Modifier.height(24.dp))

        CustomButton(
            text = "Sign In",
            backgroundColor = Teal,
            textColor = White,
            onClick = {
                viewModel.login(username.value, password.value)
            }
        )
        Spacer(Modifier.height(24.dp))
    }

}


@SuppressLint("UnrememberedMutableState")
@Composable
fun AuthPasswordTextField(state: MutableState<String>, hint: String) {
    var isPasswordVisible by remember { mutableStateOf(false) }
    CustomOutlinedTextField(
        state = state,
        hint = hint,
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val icon =
                if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                Icon(
                    imageVector = icon,
                    contentDescription = "Toggle password visibility"
                )
            }
        },
    )
}