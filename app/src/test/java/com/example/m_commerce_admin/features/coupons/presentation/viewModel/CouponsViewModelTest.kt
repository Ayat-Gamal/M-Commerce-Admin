package com.example.m_commerce_admin.features.coupons.presentation.viewModel

import com.example.m_commerce_admin.features.coupons.domain.entity.CouponInput
import com.example.m_commerce_admin.features.coupons.domain.entity.CouponItem
import com.example.m_commerce_admin.features.coupons.domain.entity.DiscountType
import com.example.m_commerce_admin.features.coupons.domain.usecase.AddCouponUseCase
import com.example.m_commerce_admin.features.coupons.domain.usecase.DeleteCouponUseCase
import com.example.m_commerce_admin.features.coupons.domain.usecase.GetAllCouponsUseCase
import com.example.m_commerce_admin.features.coupons.domain.usecase.UpdateCouponUseCase
import com.example.m_commerce_admin.features.coupons.presentation.states.CouponFormState
import io.mockk.coEvery
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CouponsViewModelTest {

    private val getAllCouponsUseCase: GetAllCouponsUseCase = mockk<GetAllCouponsUseCase>()
    private val addCouponUseCase: AddCouponUseCase = mockk<AddCouponUseCase>()
    private val updateCouponUseCase: UpdateCouponUseCase = mockk<UpdateCouponUseCase>()
    private val deleteCouponUseCase: DeleteCouponUseCase = mockk<DeleteCouponUseCase>()
    private lateinit var couponsViewModel: CouponsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        couponsViewModel = CouponsViewModel(
            getAllCouponsUseCase,
            addCouponUseCase,
            updateCouponUseCase,
            deleteCouponUseCase,
        )


    }
    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun detach() {
        Dispatchers.resetMain()
    }


    @Test
    fun `addCoupon sets state to Error when use case returns failure`() = runTest {
        val coupon = CouponInput(
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
            appliesOncePerCustomer = true
        )

        coEvery { addCouponUseCase(coupon) } throws Exception("Simulated Failure")


        couponsViewModel.addCoupon(coupon)

        advanceUntilIdle()

        assertTrue(couponsViewModel.couponFormState.value is CouponFormState.Error)
    }


    @Test
    fun `addCoupon sets state to Success when use case returns success`() = runTest {
        val coupon = CouponInput(
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
            appliesOncePerCustomer = true
        )

        coEvery { addCouponUseCase(coupon) } returns Result.success(Unit)

        coEvery { getAllCouponsUseCase(Unit) } returns flowOf(emptyList())

        couponsViewModel.addCoupon(coupon)
        advanceUntilIdle()


        assertEquals(CouponFormState.Success, couponsViewModel.couponFormState.value)
    }

    @Test
    fun `updateCoupon sets state to Success when updateCouponUseCase returns success`() = runTest {
        val coupon = mockCoupon()

        coEvery { updateCouponUseCase(coupon) } returns Result.success(Unit)
        coEvery { getAllCouponsUseCase(Unit) } returns flowOf(emptyList())

        couponsViewModel.updateCoupon(coupon)
        advanceUntilIdle()
        val res = couponsViewModel.lastCouponInput?.id
        assertEquals(res, coupon.id)
    }

    @Test
    fun `deleteCoupon sets state to Success when deleteCouponUseCase returns success`() = runTest {
        val couponCode = "DELETE123"

        coEvery { deleteCouponUseCase(couponCode) } returns Result.success(Unit)
        coEvery { getAllCouponsUseCase(Unit) } returns flowOf(emptyList())

        couponsViewModel.deleteCoupon(couponCode)

        advanceUntilIdle()
        val res = couponsViewModel.lastDeleteCode
        assertEquals(res, couponCode)
    }

    @Test
    fun `fetchAllCoupons populates coupons from use case`() = runTest {
        val fakeCoupons = listOf(
            CouponItem(
                code = "COUPON1",
                startsAt = "2025-06-01",
                endsAt = "2025-06-30",
                title = "New Coupon",
                summary = "Save big!",
                usageLimit = 10,
                currencyCode = "USD",
                id = "id",
                value = 20.2,
                usedCount = 2,
                createdAt = "2025:03:25",
                updatedAt = "2025:03:25",
                amount = 3.0,
            ),
            CouponItem(
                code = "COUPON2",
                startsAt = "2025-06-01",
                endsAt = "2025-06-30",
                title = "New Coupon",
                summary = "Save big!",
                usageLimit = 10,
                currencyCode = "USD",
                id = "id",
                value = 20.2,
                usedCount = 2,
                createdAt = "2025:03:25",
                updatedAt = "2025:03:25",
                amount = 3.0,
            )

        )

        coEvery { getAllCouponsUseCase(Unit) } returns flowOf(fakeCoupons)

        couponsViewModel.fetchAllCoupons()
        advanceUntilIdle()

        val result = couponsViewModel.coupons.value
        assertEquals(2, result.size)
        assertEquals("COUPON1", result[0].code)
        assertEquals("COUPON2", result[1].code)
    }

}

private fun mockCoupon() = CouponInput(
    id = "coupon-id",
    title = "Test Coupon",
    summary = "Test Summary",
    code = "TEST100",
    startsAt = "2025-06-01",
    endsAt = "2025-06-30",
    usageLimit = 10,
    currencyCode = "USD",
    discountType = DiscountType.PERCENTAGE,
    discountValue = 20.0,
    appliesOncePerCustomer = true
)
