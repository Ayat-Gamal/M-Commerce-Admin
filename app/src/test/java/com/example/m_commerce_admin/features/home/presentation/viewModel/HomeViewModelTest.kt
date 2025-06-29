package com.example.m_commerce_admin.features.home.presentation.viewModel

import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.m_commerce_admin.features.home.domain.entity.Order
import com.example.m_commerce_admin.features.home.domain.usecase.GetLastOrdersUseCase
import com.example.m_commerce_admin.features.home.presentation.state.HomeState
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class HomeViewModelTest {


    private val getLastOrderUseCase: GetLastOrdersUseCase = mockk<GetLastOrdersUseCase>()
    private lateinit var homeViewModel: HomeViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun detach() {
        Dispatchers.resetMain()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `fetchOrders emits Success state with orders`() = runTest {
        val mockOrders = listOf(
            Order(
                id = "1", customerName = "ayatt", totalAmount = "100.0",
                name = "order one", currency = "USD",
                createdAt = "2025:05:22", status = "Done",
                customerEmail = "example@gamil.com"
            )
        )
        val successfulState = HomeState.Success(mockOrders)

        coEvery { getLastOrderUseCase(Unit) } returns flow {
            emit(successfulState)
        }
        //becauese i use init { }
        homeViewModel = HomeViewModel(getLastOrderUseCase)

        homeViewModel.fetchOrders()
        advanceUntilIdle()

        assertEquals(successfulState, homeViewModel.orderState.value)
    }


}