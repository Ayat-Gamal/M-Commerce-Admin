package com.example.m_commerce_admin.features.login.presentation.viewModel

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.m_commerce_admin.features.home.domain.usecase.GetLastOrdersUseCase
import com.example.m_commerce_admin.features.home.presentation.viewModel.HomeViewModel
import com.example.m_commerce_admin.features.login.data.dataStore.AdminPreferences
import com.example.m_commerce_admin.features.login.domain.entity.AdminUser
import com.example.m_commerce_admin.features.login.domain.usecase.LoginUseCase
import com.example.m_commerce_admin.features.login.presentation.state.LoginState
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.log


@RunWith(AndroidJUnit4::class)

class LoginViewModelTest {
    private val adminPreferences: AdminPreferences = mockk(relaxed = true)
    private val loginUseCase: LoginUseCase = mockk<LoginUseCase>()
    private lateinit var loginViewModel: LoginViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())

        loginViewModel = LoginViewModel(loginUseCase, adminPreferences)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun detach() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login to app with valid data`() = runTest {
        val admin = AdminUser(
            username = "admin",
            password = "1234"
        )
        coEvery { loginUseCase(admin) } returns true
        loginViewModel.login(admin.username, admin.password)
        advanceUntilIdle()
        assertTrue(loginViewModel.isLogged.value)
    }

    @Test
    fun `login fails with invalid credentials`() = runTest {
        val admin = AdminUser("admin", "wrongpass")

        coEvery { loginUseCase(admin) } returns false

        loginViewModel.login(admin.username, admin.password)

        advanceUntilIdle()

        assertFalse(loginViewModel.isLogged.value)
        assertTrue(loginViewModel.uiState.value is LoginState.Error)
    }

}