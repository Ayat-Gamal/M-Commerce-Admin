package com.example.m_commerce_admin.features.coupons.data.remote

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.exception.ApolloException
import com.example.m_commerce_admin.GetCouponsQuery
import com.example.m_commerce_admin.features.coupons.data.model.DiscountCode
import com.example.m_commerce_admin.features.coupons.data.model.DiscountCodeRequest
import com.example.m_commerce_admin.features.coupons.data.model.PriceRule
import com.example.m_commerce_admin.features.coupons.data.model.PriceRuleRequest
import com.example.m_commerce_admin.features.coupons.data.model.toCouponItems
import com.example.m_commerce_admin.features.coupons.data.remote.service.ShopifyCouponApi
import com.example.m_commerce_admin.features.coupons.domain.entity.CouponInput
import com.example.m_commerce_admin.features.coupons.domain.entity.CouponItem
import com.example.m_commerce_admin.features.coupons.domain.entity.DiscountType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CouponRemoteDataSourceImpl @Inject constructor(
    private val apolloClient: ApolloClient,
    private val shopifyCouponApi: ShopifyCouponApi
) : CouponRemoteDataSource {
    override fun getCoupons(): Flow<List<CouponItem>> = flow {
        try {
            val response = apolloClient.query(GetCouponsQuery(first = 20)).execute()
            if (response.hasErrors()) {
                emit(emptyList())
                return@flow
            }
            val data = response.data
            if (data != null) {
                emit(data.toCouponItems())
            } else {
                emit(emptyList())
            }
        } catch (e: ApolloException) {
            emit(emptyList())
        }
    }

    override suspend fun addCoupon(input: CouponInput): Result<Unit> {
        return try {
            val value = when (input.discountType) {
                DiscountType.FIXED_AMOUNT -> "-${input.discountValue}"
                DiscountType.PERCENTAGE -> "-${input.discountValue}"
            }

            val valueType = when (input.discountType) {
                DiscountType.FIXED_AMOUNT -> "fixed_amount"
                DiscountType.PERCENTAGE -> "percentage"
            }

            val priceRule = PriceRule(
                title = input.title ?: "",
                value = value,
                value_type = valueType,
                starts_at = input.startsAt ?: "",
                ends_at = input.endsAt ?: "",
                usage_limit = input.usageLimit ?: 1,
                target_type = "line_item",
                target_selection = "all",
                allocation_method = "across",
                customer_selection = "all"
            )

            val priceRuleResponse = shopifyCouponApi.createPriceRule(PriceRuleRequest(priceRule))
            if (!priceRuleResponse.isSuccessful) {
                return Result.failure(
                    Exception(
                        "Failed to create price rule: ${
                            priceRuleResponse.errorBody()?.string()
                        }"
                    )
                )
            }

            val priceRuleId = priceRuleResponse.body()?.price_rule?.id
                ?: return Result.failure(Exception("Price rule ID is missing"))

            val discountCode = DiscountCode(code = input.code ?: "")
            val discountCodeRequest = DiscountCodeRequest(discount_code = discountCode)

            val discountResponse =
                shopifyCouponApi.createDiscountCode(priceRuleId, discountCodeRequest)
            if (!discountResponse.isSuccessful) {
                return Result.failure(
                    Exception(
                        "Failed to create discount code: ${
                            discountResponse.errorBody()?.string()
                        }"
                    )
                )
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

} 