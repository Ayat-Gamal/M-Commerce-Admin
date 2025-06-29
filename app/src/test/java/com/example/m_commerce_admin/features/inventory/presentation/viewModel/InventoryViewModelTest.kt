package com.example.m_commerce_admin.features.inventory.presentation.viewModel

import com.example.m_commerce_admin.features.inventory.domain.entity.InventoryLevel
import com.example.m_commerce_admin.features.inventory.domain.usecase.AdjustInventoryUseCase
import com.example.m_commerce_admin.features.inventory.domain.usecase.GetInventoryLevelsUseCase
import com.example.m_commerce_admin.features.inventory.domain.usecase.GetProductsForInventoryUseCase
import com.example.m_commerce_admin.features.inventory.domain.usecase.params.AdjustInventoryLevelParam
import com.example.m_commerce_admin.features.inventory.domain.usecase.params.GetProductsForInventoryParams
import com.example.m_commerce_admin.features.inventory.presentation.state.InventoryLevelsState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class InventoryViewModelTest {

    private val getInventoryLevelsUseCase: GetInventoryLevelsUseCase =
        mockk<GetInventoryLevelsUseCase>()
    private val adjustInventoryUseCase: AdjustInventoryUseCase = mockk<AdjustInventoryUseCase>()
    private val getProductsForInventoryUseCase: GetProductsForInventoryUseCase =
        mockk<GetProductsForInventoryUseCase>()


    private lateinit var inventoryViewModel: InventoryViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())

        coEvery { getInventoryLevelsUseCase(Unit) } returns flowOf(Result.success(emptyList()))
        inventoryViewModel = InventoryViewModel(
            getInventoryLevelsUseCase,
            adjustInventoryUseCase,
            getProductsForInventoryUseCase,
        )

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun detach() {
        Dispatchers.resetMain()
    }


    @Test
    fun `adjustInventoryLevel updates state on success`() = runTest {
        // Arrange
        val inventoryItemId = 123L
        val adjustment = 5
        val expectedLevel = InventoryLevel(
            inventoryItemId = 1L,
            available = 5,
            locationId = 42444,
            updatedAt = "2025:03:25",
            graphqlApiId = "dsa1222",
            productTitle = "title",
            productImage = "url image",
            productPrice = "250",
            productSku = "SKU"
        )

        coEvery {
            adjustInventoryUseCase(AdjustInventoryLevelParam(inventoryItemId, adjustment))
        } returns expectedLevel

        // Act
        inventoryViewModel.adjustInventoryLevel(inventoryItemId, adjustment)
        advanceUntilIdle()

        // Assert
        val state = inventoryViewModel.adjustState.value
        if (state != null) {
            assertTrue(state.isSuccess)
        }
        assertEquals(expectedLevel, state?.getOrNull() ?: state?.isSuccess)

        assertEquals(false, inventoryViewModel.isAdjusting.value)

        coVerify { adjustInventoryUseCase(AdjustInventoryLevelParam(inventoryItemId, adjustment)) }
    }

    @Test
    fun `adjustInventoryLevel updates state on failure`() = runTest {
        val inventoryItemId = 123L
        val adjustment = 5
        val exception = Exception("Adjustment failed")

        coEvery {
            adjustInventoryUseCase(AdjustInventoryLevelParam(inventoryItemId, adjustment))
        } throws exception

        inventoryViewModel.adjustInventoryLevel(inventoryItemId, adjustment)
        advanceUntilIdle()

        val state = inventoryViewModel.adjustState.value
        state?.isFailure?.let { assertTrue(it) }
        assertEquals("Adjustment failed", state?.exceptionOrNull()?.message)
        assertEquals(false, inventoryViewModel.isAdjusting.value)
    }

    @Test
    fun `loadInventoryData emits inventory levels from use case`() = runTest {
        // Given
        val mockInventory = listOf(
            InventoryLevel(
                inventoryItemId = 1L,
                available = 5,
                locationId = 42444,
                updatedAt = "2025:03:25",
                graphqlApiId = "dsa1222",
                productTitle = "title",
                productImage = "url image",
                productPrice = "250",
                productSku = "SKU"
            ),
            InventoryLevel(
                inventoryItemId = 2L,
                available = 5,
                locationId = 42444,
                updatedAt = "2025:03:25",
                graphqlApiId = "dsa1222",
                productTitle = "title",
                productImage = "url image",
                productPrice = "250",
                productSku = "SKU"
            )

        )

        coEvery { getInventoryLevelsUseCase(Unit) } returns flowOf(Result.success(mockInventory))


        inventoryViewModel.loadInventoryData()
        advanceUntilIdle()


        assertEquals(InventoryLevelsState.Success(mockInventory), inventoryViewModel.uiState.value)
    }

    @Test
    fun `loadInventoryData sets UI state to success when inventory levels are returned`() =
        runTest {

            val levels = listOf(
                InventoryLevel(
                    inventoryItemId = 1L,
                    available = 5,
                    locationId = 42444,
                    updatedAt = "2025:03:25",
                    graphqlApiId = "dsa1222",
                    productTitle = "title",
                    productImage = "url image",
                    productPrice = "250",
                    productSku = "SKU"
                ),
                InventoryLevel(
                    inventoryItemId = 2L,
                    available = 5,
                    locationId = 42444,
                    updatedAt = "2025:03:25",
                    graphqlApiId = "dsa1222",
                    productTitle = "title",
                    productImage = "url image",
                    productPrice = "250",
                    productSku = "SKU"
                )
            )
            coEvery { getInventoryLevelsUseCase(Unit) } returns flowOf(Result.success(levels))


            coEvery { getProductsForInventoryUseCase(GetProductsForInventoryParams()) } returns flowOf(
                Result.success(emptyList())
            )


            inventoryViewModel.loadInventoryData()
            advanceUntilIdle()


            assertNotEquals(
                InventoryLevelsState.Error("An unknown error occurred."),
                inventoryViewModel.uiState.value
            )
            assertNotEquals(
                InventoryLevelsState.Loading,
                inventoryViewModel.uiState.value
            )
        }

}