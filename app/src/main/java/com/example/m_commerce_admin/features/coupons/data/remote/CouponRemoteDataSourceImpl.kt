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
                val errorBody = priceRuleResponse.errorBody()?.string()
                return Result.failure(
                    Exception(
                        "Failed to create price rule. Please check your internet connection and try again. Error: $errorBody"
                    )
                )
            }

            val priceRuleId = priceRuleResponse.body()?.price_rule?.id
            if (priceRuleId == null) {
                return Result.failure(Exception("Price rule ID is missing from server response. Please try again."))
            }

            val discountCode = DiscountCode(code = input.code ?: "")
            val discountCodeRequest = DiscountCodeRequest(discount_code = discountCode)

            val discountResponse =
                shopifyCouponApi.createDiscountCode(priceRuleId, discountCodeRequest)


            if (!discountResponse.isSuccessful) {
                val errorBody = discountResponse.errorBody()?.string()

                return Result.failure(
                    Exception(
                        "Failed to create discount code. Please check your internet connection and try again. Error: $errorBody"
                    )
                )
            }

            Result.success(Unit)
        } catch (e: Exception) {
            val errorMessage = e.message
            Result.failure(Exception(errorMessage))
        }
    }

    override suspend fun updateCoupon(couponInput: CouponInput): Result<Unit> {
        return try {
            val priceRulesResponse = shopifyCouponApi.getPriceRules(250)
            if (!priceRulesResponse.isSuccessful) {
                return Result.failure(
                    Exception(
                        "Failed to get price rules: ${
                            priceRulesResponse.errorBody()?.string()
                        }"
                    )
                )
            }
            val priceRules = priceRulesResponse.body()?.price_rules ?: emptyList()

            for (rule in priceRules) {
                val discountCodesResponse = shopifyCouponApi.getDiscountCodes(rule.id)
                if (!discountCodesResponse.isSuccessful) {
                    continue
                }
                val discountCodes = discountCodesResponse.body()?.discount_codes ?: emptyList()

                for (code in discountCodes) {

                    if (code.code == couponInput.code) {
                        val priceRuleId = rule.id
                        val discountCodeId = code.id

                        val value = when (couponInput.discountType) {
                            DiscountType.FIXED_AMOUNT -> "-${couponInput.discountValue}"
                            DiscountType.PERCENTAGE -> "-${couponInput.discountValue}"
                        }
                        val valueType = when (couponInput.discountType) {
                            DiscountType.FIXED_AMOUNT -> "fixed_amount"
                            DiscountType.PERCENTAGE -> "percentage"
                        }
                        val updatedPriceRule = PriceRule(
                            title = couponInput.title ?: "",
                            value = value,
                            value_type = valueType,
                            starts_at = couponInput.startsAt ?: "",
                            ends_at = couponInput.endsAt ?: "",
                            usage_limit = couponInput.usageLimit ?: 1,
                            target_type = "line_item",
                            target_selection = "all",
                            allocation_method = "across",
                            customer_selection = "all"
                        )
                        val priceRuleUpdateResponse = shopifyCouponApi.updatePriceRule(
                            priceRuleId,
                            PriceRuleRequest(updatedPriceRule)
                        )
                        if (!priceRuleUpdateResponse.isSuccessful) {
                            return Result.failure(
                                Exception(
                                    "Failed to update price rule: ${
                                        priceRuleUpdateResponse.errorBody()?.string()
                                    }"
                                )
                            )
                        }
                        val updatedDiscountCode = DiscountCode(code = couponInput.code ?: "")
                        val discountCodeUpdateResponse = shopifyCouponApi.updateDiscountCode(
                            priceRuleId,
                            discountCodeId,
                            DiscountCodeRequest(updatedDiscountCode)
                        )
                        if (!discountCodeUpdateResponse.isSuccessful) {
                            return Result.failure(
                                Exception(
                                    "Failed to update discount code: ${
                                        discountCodeUpdateResponse.errorBody()?.string()
                                    }"
                                )
                            )
                        }
                        return Result.success(Unit)
                    }
                }
            }
            return Result.failure(Exception("Coupon code '${couponInput.code}' not found in any price rule (REST-only search)"))
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun deleteCoupon(couponCode: String): Result<Unit> {
        return try {
            val priceRulesResponse = shopifyCouponApi.getPriceRules(250)
            if (!priceRulesResponse.isSuccessful) {
                return Result.failure(
                    Exception(
                        "Failed to get price rules: ${
                            priceRulesResponse.errorBody()?.string()
                        }"
                    )
                )
            }
            val priceRules = priceRulesResponse.body()?.price_rules ?: emptyList()

            for (rule in priceRules) {

                val discountCodesResponse = shopifyCouponApi.getDiscountCodes(rule.id)
                if (!discountCodesResponse.isSuccessful) {
                    continue
                }

                val discountCodes = discountCodesResponse.body()?.discount_codes ?: emptyList()

                for (code in discountCodes) {

                    if (code.code == couponCode) {

                        val priceRuleId = rule.id
                        val discountCodeId = code.id

                        val deleteDiscountResponse =
                            shopifyCouponApi.deleteDiscountCode(priceRuleId, discountCodeId)

                        if (!deleteDiscountResponse.isSuccessful) {
                            return Result.failure(
                                Exception(
                                    "Failed to delete discount code: ${
                                        deleteDiscountResponse.errorBody()?.string()
                                    }"
                                )
                            )
                        }

                        val deletePriceRuleResponse = shopifyCouponApi.deletePriceRule(priceRuleId)
                        if (!deletePriceRuleResponse.isSuccessful) {
                            return Result.failure(
                                Exception(
                                    "Failed to delete price rule: ${
                                        deletePriceRuleResponse.errorBody()?.string()
                                    }"
                                )
                            )
                        }
                        return Result.success(Unit)
                    }
                }
            }
            return Result.failure(Exception("Coupon code '$couponCode' not found"))

        } catch (e: Exception) {
            val errorMessage = e.message
            Result.failure(Exception(errorMessage))
        }
    }

    private suspend fun findPriceRuleIdBySearching(): Long? {
        return try {

            val priceRulesResponse = shopifyCouponApi.getPriceRules(250)
            if (!priceRulesResponse.isSuccessful) {
                return null
            }

            val priceRules = priceRulesResponse.body()?.price_rules
            val firstPriceRuleId = priceRules?.firstOrNull()?.id
            firstPriceRuleId

        } catch (e: Exception) {
            null
        }
    }
} 