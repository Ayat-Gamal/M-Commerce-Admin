package com.example.m_commerce_admin.features.inventory.data.repository

import com.example.m_commerce_admin.BuildConfig
import com.example.m_commerce_admin.features.inventory.data.remote.InventoryRemoteDataSource
import com.example.m_commerce_admin.features.inventory.domain.entity.InventoryLevel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class InventoryRepositoryImplTest {


    private val remoteDataSource: InventoryRemoteDataSource = mockk<InventoryRemoteDataSource>()
    private lateinit var repositoryImpl: InventoryRepositoryImpl

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        repositoryImpl = InventoryRepositoryImpl(remoteDataSource)

    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun detach() {
        Dispatchers.resetMain()
    }


    @Test
    fun `getInventoryLevel returns flow of list of inventory level`() = runTest {

        val fakeInventory = listOf(
            InventoryLevel(inventoryItemId = 1L, available = 5, locationId = 42444, updatedAt = "2025:03:25", graphqlApiId = "dsa1222", productTitle = "title", productImage = "url image", productPrice = "250", productSku = "SKU"),
            InventoryLevel(inventoryItemId = 2L, available = 5, locationId = 42444, updatedAt = "2025:03:25", graphqlApiId = "dsa1222", productTitle = "title", productImage = "url image", productPrice = "250", productSku = "SKU")

        )

        coEvery { remoteDataSource.getInventoryWithProductDetails() } returns Result.success(
            fakeInventory
        )

        val result = repositoryImpl.getInventoryLevels()


        result.collect { emitted ->
            assertTrue(emitted.isSuccess)
            assertEquals(fakeInventory, emitted.getOrNull())
        }
    }

    @Test
    fun `getInventoryLevel returns flow of failure on error`() = runTest {
        // Arrange
        val exception = Exception("Network error")
        coEvery { remoteDataSource.getInventoryWithProductDetails() } returns Result.failure(
            exception
        )

        // Act
        val result = repositoryImpl.getInventoryLevels()

        // Assert
        result.collect { emitted ->
            assertTrue(emitted.isFailure)
            assertEquals("Network error", emitted.exceptionOrNull()?.message)
        }
    }

    @Test
    fun `adjustInventoryLevel returns adjusted inventory level from remote`() = runTest {

        val inventoryItemId = 123L
        val adjustment = 5
        val expected = InventoryLevel(
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
            remoteDataSource.adjustInventoryLevel(
                inventoryItemId = inventoryItemId,
                locationId = BuildConfig.locationID.toLong(),
                availableAdjustment = adjustment
            )
        } returns expected


        val result = repositoryImpl.adjustInventoryLevel(inventoryItemId, adjustment)

        assertEquals(expected, result)
    }

}