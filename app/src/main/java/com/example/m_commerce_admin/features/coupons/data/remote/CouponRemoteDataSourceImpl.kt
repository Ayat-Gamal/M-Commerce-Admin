package com.example.m_commerce_admin.features.coupons.data.remote

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.exception.ApolloException
import com.example.m_commerce_admin.GetCouponsQuery
import com.example.m_commerce_admin.UpdateCouponMutation
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
import toGraphQL
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
            android.util.Log.d("TAG", "CouponRemoteDataSourceImpl: Adding coupon with code: ${input.code}")
            android.util.Log.d("TAG", "CouponRemoteDataSourceImpl: Coupon input: $input")
            
            val value = when (input.discountType) {
                DiscountType.FIXED_AMOUNT -> "-${input.discountValue}"
                DiscountType.PERCENTAGE -> "-${input.discountValue}"
            }

            val valueType = when (input.discountType) {
                DiscountType.FIXED_AMOUNT -> "fixed_amount"
                DiscountType.PERCENTAGE -> "percentage"
            }

            android.util.Log.d("TAG", "CouponRemoteDataSourceImpl: Creating price rule with value: $value, type: $valueType")

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

            android.util.Log.d("TAG", "CouponRemoteDataSourceImpl: Price rule object: $priceRule")

            val priceRuleResponse = shopifyCouponApi.createPriceRule(PriceRuleRequest(priceRule))
            android.util.Log.d("TAG", "CouponRemoteDataSourceImpl: Price rule response successful: ${priceRuleResponse.isSuccessful}")
            android.util.Log.d("TAG", "CouponRemoteDataSourceImpl: Price rule response code: ${priceRuleResponse.code()}")
            
            if (!priceRuleResponse.isSuccessful) {
                val errorBody = priceRuleResponse.errorBody()?.string()
                android.util.Log.e("TAG", "CouponRemoteDataSourceImpl: Failed to create price rule. Error: $errorBody")
                return Result.failure(
                    Exception(
                        "Failed to create price rule. Please check your internet connection and try again. Error: $errorBody"
                    )
                )
            }

            val priceRuleId = priceRuleResponse.body()?.price_rule?.id
            android.util.Log.d("TAG", "CouponRemoteDataSourceImpl: Created price rule with ID: $priceRuleId")
            
            if (priceRuleId == null) {
                android.util.Log.e("TAG", "CouponRemoteDataSourceImpl: Price rule ID is missing from response")
                return Result.failure(Exception("Price rule ID is missing from server response. Please try again."))
            }

            android.util.Log.d("TAG", "CouponRemoteDataSourceImpl: Creating discount code: ${input.code}")

            val discountCode = DiscountCode(code = input.code ?: "")
            val discountCodeRequest = DiscountCodeRequest(discount_code = discountCode)

            android.util.Log.d("TAG", "CouponRemoteDataSourceImpl: Discount code request: $discountCodeRequest")

            val discountResponse = shopifyCouponApi.createDiscountCode(priceRuleId, discountCodeRequest)
            android.util.Log.d("TAG", "CouponRemoteDataSourceImpl: Discount code response successful: ${discountResponse.isSuccessful}")
            android.util.Log.d("TAG", "CouponRemoteDataSourceImpl: Discount code response code: ${discountResponse.code()}")
            
            if (!discountResponse.isSuccessful) {
                val errorBody = discountResponse.errorBody()?.string()
                android.util.Log.e("TAG", "CouponRemoteDataSourceImpl: Failed to create discount code. Error: $errorBody")
                return Result.failure(
                    Exception(
                        "Failed to create discount code. Please check your internet connection and try again. Error: $errorBody"
                    )
                )
            }

            android.util.Log.d("TAG", "CouponRemoteDataSourceImpl: Successfully created coupon with code: ${input.code}")
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("TAG", "CouponRemoteDataSourceImpl: Exception during add coupon", e)
            val errorMessage = when {
                e.message?.contains("timeout", ignoreCase = true) == true -> 
                    "Network timeout. Please check your internet connection and try again."
                e.message?.contains("connection", ignoreCase = true) == true -> 
                    "Network connection error. Please check your internet connection and try again."
                else -> "Failed to create coupon: ${e.message ?: "Unknown error"}"
            }
            Result.failure(Exception(errorMessage))
        }
    }

    override suspend fun updateCoupon(couponInput: CouponInput): Result<Unit> {
        return try {
            android.util.Log.d("TAG", "CouponRemoteDataSourceImpl: REST-only update for coupon code: ${couponInput.code}")

            // 1. Get all price rules
            val priceRulesResponse = shopifyCouponApi.getPriceRules(250)
            if (!priceRulesResponse.isSuccessful) {
                return Result.failure(Exception("Failed to get price rules: ${priceRulesResponse.errorBody()?.string()}"))
            }
            val priceRules = priceRulesResponse.body()?.price_rules ?: emptyList()
            android.util.Log.d("TAG", "CouponRemoteDataSourceImpl: Found ${priceRules.size} price rules")

            // 2. For each price rule, get its discount codes and look for a match
            for (rule in priceRules) {
                android.util.Log.d("TAG", "CouponRemoteDataSourceImpl: Checking price rule ID: ${rule.id}, title: ${rule.title}")
                val discountCodesResponse = shopifyCouponApi.getDiscountCodes(rule.id)
                if (!discountCodesResponse.isSuccessful) {
                    android.util.Log.e("TAG", "CouponRemoteDataSourceImpl: Failed to get discount codes for price rule ${rule.id}")
                    continue
                }
                val discountCodes = discountCodesResponse.body()?.discount_codes ?: emptyList()
                android.util.Log.d("TAG", "CouponRemoteDataSourceImpl: Found ${discountCodes.size} discount codes for price rule ${rule.id}")
                
                for (code in discountCodes) {
                    android.util.Log.d("TAG", "CouponRemoteDataSourceImpl: Checking discount code: ${code.code} vs searching for: ${couponInput.code}")
                    if (code.code == couponInput.code) {
                        // Found the price rule and discount code to update!
                        val priceRuleId = rule.id
                        val discountCodeId = code.id
                        android.util.Log.d("TAG", "CouponRemoteDataSourceImpl: Found priceRuleId=$priceRuleId, discountCodeId=$discountCodeId for code=${code.code}")

                        // Prepare the updated price rule
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
                        // 3. Update the price rule
                        val priceRuleUpdateResponse = shopifyCouponApi.updatePriceRule(priceRuleId, PriceRuleRequest(updatedPriceRule))
                        if (!priceRuleUpdateResponse.isSuccessful) {
                            return Result.failure(Exception("Failed to update price rule: ${priceRuleUpdateResponse.errorBody()?.string()}"))
                        }
                        // 4. Update the discount code
                        val updatedDiscountCode = DiscountCode(code = couponInput.code ?: "")
                        val discountCodeUpdateResponse = shopifyCouponApi.updateDiscountCode(
                            priceRuleId,
                            discountCodeId,
                            DiscountCodeRequest(updatedDiscountCode)
                        )
                        if (!discountCodeUpdateResponse.isSuccessful) {
                            return Result.failure(Exception("Failed to update discount code: ${discountCodeUpdateResponse.errorBody()?.string()}"))
                        }
                        android.util.Log.d("TAG", "CouponRemoteDataSourceImpl: Update successful via REST-only flow")
                        return Result.success(Unit)
                    }
                }
            }
            android.util.Log.e("TAG", "CouponRemoteDataSourceImpl: Coupon code '${couponInput.code}' not found in any price rule")
            return Result.failure(Exception("Coupon code '${couponInput.code}' not found in any price rule (REST-only search)"))
        } catch (e: Exception) {
            android.util.Log.e("TAG", "CouponRemoteDataSourceImpl: Exception during REST-only update", e)
            return Result.failure(e)
        }
    }

    override suspend fun deleteCoupon(couponCode: String): Result<Unit> {
        return try {
            android.util.Log.d("TAG", "CouponRemoteDataSourceImpl: Starting delete for coupon code: $couponCode")

            // 1. Get all price rules
            val priceRulesResponse = shopifyCouponApi.getPriceRules(250)
            if (!priceRulesResponse.isSuccessful) {
                return Result.failure(Exception("Failed to get price rules: ${priceRulesResponse.errorBody()?.string()}"))
            }
            val priceRules = priceRulesResponse.body()?.price_rules ?: emptyList()
            android.util.Log.d("TAG", "CouponRemoteDataSourceImpl: Found ${priceRules.size} price rules")

            // 2. For each price rule, get its discount codes and look for a match
            for (rule in priceRules) {
                android.util.Log.d("TAG", "CouponRemoteDataSourceImpl: Checking price rule ID: ${rule.id}, title: ${rule.title}")
                val discountCodesResponse = shopifyCouponApi.getDiscountCodes(rule.id)
                if (!discountCodesResponse.isSuccessful) {
                    android.util.Log.e("TAG", "CouponRemoteDataSourceImpl: Failed to get discount codes for price rule ${rule.id}")
                    continue
                }
                val discountCodes = discountCodesResponse.body()?.discount_codes ?: emptyList()
                android.util.Log.d("TAG", "CouponRemoteDataSourceImpl: Found ${discountCodes.size} discount codes for price rule ${rule.id}")
                
                for (code in discountCodes) {
                    android.util.Log.d("TAG", "CouponRemoteDataSourceImpl: Checking discount code: ${code.code} vs searching for: $couponCode")
                    if (code.code == couponCode) {
                        // Found the price rule and discount code to delete!
                        val priceRuleId = rule.id
                        val discountCodeId = code.id
                        android.util.Log.d("TAG", "CouponRemoteDataSourceImpl: Found priceRuleId=$priceRuleId, discountCodeId=$discountCodeId for code=${code.code}")

                        // 3. Delete the discount code first
                        val deleteDiscountResponse = shopifyCouponApi.deleteDiscountCode(priceRuleId, discountCodeId)
                        if (!deleteDiscountResponse.isSuccessful) {
                            return Result.failure(Exception("Failed to delete discount code: ${deleteDiscountResponse.errorBody()?.string()}"))
                        }
                        android.util.Log.d("TAG", "CouponRemoteDataSourceImpl: Successfully deleted discount code")

                        // 4. Delete the price rule
                        val deletePriceRuleResponse = shopifyCouponApi.deletePriceRule(priceRuleId)
                        if (!deletePriceRuleResponse.isSuccessful) {
                            return Result.failure(Exception("Failed to delete price rule: ${deletePriceRuleResponse.errorBody()?.string()}"))
                        }
                        android.util.Log.d("TAG", "CouponRemoteDataSourceImpl: Successfully deleted price rule")

                        android.util.Log.d("TAG", "CouponRemoteDataSourceImpl: Delete successful for coupon code: $couponCode")
                        return Result.success(Unit)
                    }
                }
            }
            android.util.Log.e("TAG", "CouponRemoteDataSourceImpl: Coupon code '$couponCode' not found in any price rule")
            return Result.failure(Exception("Coupon code '$couponCode' not found"))
        } catch (e: Exception) {
            android.util.Log.e("TAG", "CouponRemoteDataSourceImpl: Exception during delete coupon", e)
            val errorMessage = when {
                e.message?.contains("timeout", ignoreCase = true) == true -> 
                    "Network timeout. Please check your internet connection and try again."
                e.message?.contains("connection", ignoreCase = true) == true -> 
                    "Network connection error. Please check your internet connection and try again."
                else -> "Failed to delete coupon: ${e.message ?: "Unknown error"}"
            }
            Result.failure(Exception(errorMessage))
        }
    }

    private suspend fun getPriceRuleIdFromCouponId(couponId: String?): Long? {
        if (couponId == null) return null
        
        android.util.Log.d("TAG", "CouponRemoteDataSourceImpl: Getting price rule ID for coupon ID: $couponId")
        
        return when {
            // If it's already a PriceRule ID
            couponId.contains("PriceRule/") -> {
                val priceRuleId = couponId.split("PriceRule/").lastOrNull()?.toLongOrNull()
                android.util.Log.d("TAG", "CouponRemoteDataSourceImpl: Extracted price rule ID from PriceRule format: $priceRuleId")
                priceRuleId
            }
            // If it's a direct numeric ID (assume it's a price rule ID)
            couponId.matches(Regex("^\\d+$")) -> {
                val priceRuleId = couponId.toLongOrNull()
                android.util.Log.d("TAG", "CouponRemoteDataSourceImpl: Using direct numeric ID as price rule ID: $priceRuleId")
                priceRuleId
            }
            // For any other format, try to find by searching price rules
            else -> {
                android.util.Log.d("TAG", "CouponRemoteDataSourceImpl: Unknown ID format, searching for price rule...")
                findPriceRuleIdBySearching()
            }
        }
    }

    private suspend fun findPriceRuleIdBySearching(): Long? {
        return try {
            // Get all price rules
            val priceRulesResponse = shopifyCouponApi.getPriceRules(250)
            if (!priceRulesResponse.isSuccessful) {
                android.util.Log.e("TAG", "CouponRemoteDataSourceImpl: Failed to get price rules: ${priceRulesResponse.errorBody()?.string()}")
                return null
            }
            
            val priceRules = priceRulesResponse.body()?.price_rules
            android.util.Log.d("TAG", "CouponRemoteDataSourceImpl: Found ${priceRules?.size} price rules")
            
            // For now, return the first price rule ID as a fallback
            // In a real implementation, you would match by coupon code or other criteria
            val firstPriceRuleId = priceRules?.firstOrNull()?.id
            android.util.Log.d("TAG", "CouponRemoteDataSourceImpl: Using first price rule ID as fallback: $firstPriceRuleId")
            firstPriceRuleId
        } catch (e: Exception) {
            android.util.Log.e("TAG", "CouponRemoteDataSourceImpl: Exception while searching for price rule", e)
            null
        }
    }
} 