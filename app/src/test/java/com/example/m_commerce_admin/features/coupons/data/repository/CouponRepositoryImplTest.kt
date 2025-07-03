package com.example.m_commerce_admin.features.coupons.data.repository

import com.example.m_commerce_admin.features.coupons.data.remote.CouponRemoteDataSource
import com.example.m_commerce_admin.features.coupons.domain.entity.CouponInput
import com.example.m_commerce_admin.features.coupons.domain.entity.CouponItem
import com.example.m_commerce_admin.features.coupons.domain.entity.DiscountType
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
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

class CouponRepositoryImplTest {


    private val remoteDataSource: CouponRemoteDataSource = mockk<CouponRemoteDataSource>()
    private lateinit var repositoryImpl: CouponRepositoryImpl

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        repositoryImpl = CouponRepositoryImpl(remoteDataSource)

    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun detach() {
        Dispatchers.resetMain()
    }


    @Test
    fun `get coupon that returns flow of list of couponItem`() = runTest {
        val mockCoupons = listOf(
            CouponItem(
                id = "coupon_001",
                code = "AYAT",
                value = 100.2,
                usedCount = 5,
                startsAt = "2025:05:25",
                endsAt = "2025:05:25",
                title = "title",
                summary = "Summary",
                usageLimit = 5,
                createdAt = "2025:05:25",
                updatedAt = "2025:05:25",
                amount = 2.4,
                currencyCode = "USD"
            )
        )

        every { remoteDataSource.getCoupons() } returns flowOf(
            mockCoupons
        )


        val result = repositoryImpl.getCoupons()

        result.collect { res ->
            assertEquals(mockCoupons.get(0).code, "AYAT")
            assertEquals(1, res.size)
            assertEquals("AYAT", res[0].code)
            res[0].value?.let { assertEquals(100.2, it, 0.01) }
        }
    }

    @Test
    fun `addCoupon returns success result`() = runTest {
        val input = CouponInput(
            code = "NEW100",
            startsAt = "2025-06-01",
            endsAt = "2025-06-30",
            title = "New Coupon",
            summary = "Save big!",
            usageLimit = 10,
            currencyCode = "USD",
            id = "id",
            discountType = DiscountType.PERCENTAGE,
            discountValue = 20.0,
            appliesOncePerCustomer = true,
        )

        val expectedResult = Result.success(Unit)

        coEvery { remoteDataSource.addCoupon(input) } returns expectedResult

        val result = repositoryImpl.addCoupon(input)

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { remoteDataSource.addCoupon(input) }
    }

    @Test
    fun `updateCoupon returns success result`() = runTest {
        val input = CouponInput(
            code = "NEW100",
            startsAt = "2025-06-01",
            endsAt = "2025-06-30",
            title = "New Coupon",
            summary = "Save big!",
            usageLimit = 10,
            currencyCode = "USD",
            id = "id",
            discountType = DiscountType.PERCENTAGE,
            discountValue = 20.0,
            appliesOncePerCustomer = true,
        )

        val expectedResult = Result.success(Unit)

        coEvery { remoteDataSource.updateCoupon(input) } returns expectedResult

        val result = repositoryImpl.updateCoupon(input)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `deleteCoupon returns success result`() = runTest {
        val couponCode = "DELETE20"
        val expectedResult = Result.success(Unit)

        coEvery { remoteDataSource.deleteCoupon(couponCode) } returns expectedResult

        val result = repositoryImpl.deleteCoupon(couponCode)

        assertTrue(result.isSuccess)
    }

}
