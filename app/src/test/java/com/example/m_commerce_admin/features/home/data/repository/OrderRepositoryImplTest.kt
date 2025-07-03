package com.example.m_commerce_admin.features.home.data.repository

import com.example.m_commerce_admin.features.home.data.remote.OrderRemoteDataSource
import com.example.m_commerce_admin.features.home.domain.entity.Order
import com.example.m_commerce_admin.features.home.presentation.state.HomeState
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(org.junit.runners.JUnit4::class)
class OrderRepositoryImplTest {

    private val remoteDataSource: OrderRemoteDataSource = mockk<OrderRemoteDataSource>()
    private lateinit var repositoryImpl: OrderRepositoryImpl

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        repositoryImpl = OrderRepositoryImpl(remoteDataSource)

    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun detach() {
        Dispatchers.resetMain()
    }

    @Test
    fun `get Last Orders returns a flow of HomeState has list of Orders`() = runTest {
        val mockOrders = listOf(
            Order(
                id = "1", customerName = "Alice", totalAmount = "100.0",
                name = "order one",
                currency = "USD",
                createdAt = "2025:05:22",
                status = "Done",
                customerEmail = "example@gamil.com",
            ),
            Order(
                id = "2", customerName = "Bob", totalAmount = "150.0", name = "order one",
                currency = "USD",
                createdAt = "2025:05:22",
                status = "Done",
                customerEmail = "example@gamil.com",
            )
        )
        val mockState = HomeState.Success(mockOrders)

        every { remoteDataSource.getLastOrders() } returns flowOf(
            mockState
        )


        val result = repositoryImpl.getLastOrders()

        result.collect { res ->
            assertEquals(mockState, res)
        }

    }

    @Test
    fun `getLastOrders returns a flow of HomeState Error when remote fails`() = runTest {
        val errorMessage = "Failed to fetch orders"
        val mockErrorState = HomeState.Error(errorMessage)

        every { remoteDataSource.getLastOrders() } returns flowOf(mockErrorState)


        val resultFlow = repositoryImpl.getLastOrders()

        resultFlow.collect { result ->
            assertTrue(result is HomeState.Error)
            assertEquals(errorMessage, (result as HomeState.Error).message)
        }

        verify(exactly = 1) { remoteDataSource.getLastOrders() }
    }

}