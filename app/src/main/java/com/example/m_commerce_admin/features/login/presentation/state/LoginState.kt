package com.example.m_commerce_admin.features.login.presentation.state

sealed class LoginState {
     data class Error( val message: String): LoginState()
    data class Success(val user: String?): LoginState()
    data object Idle : LoginState()
}