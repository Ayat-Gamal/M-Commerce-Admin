package com.example.m_commerce_admin.features.login.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.m_commerce_admin.features.login.domain.entity.AdminUser
import com.example.m_commerce_admin.features.login.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginState>(LoginState.Idle)
    val uiState: StateFlow<LoginState> = _uiState

    private val _messageState = MutableStateFlow("")
    val messageState: StateFlow<String> = _messageState

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            showMessage("Please fill all fields")
            return
        }

        _uiState.value = LoginState.Idle

        viewModelScope.launch {
            val admin = AdminUser(username = username, password = password)
            val result = loginUseCase(admin)

            if (result) {
                _uiState.value = LoginState.Success(username)

            } else {
                _uiState.value = LoginState.Error("Invalid admin credentials")
            }
        }
    }
    fun showMessage(msg: String) {
        viewModelScope.launch {
            _messageState.emit(msg)
        }
    }
    fun clearMessage() {
        viewModelScope.launch {
            _messageState.emit("")
        }
    }

}
