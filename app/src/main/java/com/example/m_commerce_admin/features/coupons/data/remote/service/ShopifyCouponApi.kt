package com.example.m_commerce_admin.features.coupons.data.remote.service

import com.example.m_commerce_admin.features.coupons.data.model.DiscountCodeRequest
import com.example.m_commerce_admin.features.coupons.data.model.DiscountCodeResponse
import com.example.m_commerce_admin.features.coupons.data.model.PriceRuleRequest
import com.example.m_commerce_admin.features.coupons.data.model.PriceRuleResponse
import com.example.m_commerce_admin.features.coupons.data.model.PriceRulesListResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ShopifyCouponApi {
    @POST("price_rules.json")
    suspend fun createPriceRule(@Body body: PriceRuleRequest): Response<PriceRuleResponse>

    @POST("price_rules/{price_rule_id}/discount_codes.json")
    suspend fun createDiscountCode(
        @Path("price_rule_id") priceRuleId: Long,
        @Body body: DiscountCodeRequest
    ): Response<DiscountCodeResponse>

    @GET("price_rules.json")
    suspend fun getPriceRules(@Query("limit") limit: Int = 250): Response<PriceRulesListResponse>

    @GET("price_rules/{price_rule_id}.json")
    suspend fun getPriceRule(@Path("price_rule_id") priceRuleId: Long): Response<PriceRuleResponse>

    @GET("price_rules/{price_rule_id}/discount_codes.json")
    suspend fun getDiscountCodes(@Path("price_rule_id") priceRuleId: Long): Response<DiscountCodeResponse>

    @PUT("price_rules/{price_rule_id}.json")
    suspend fun updatePriceRule(
        @Path("price_rule_id") priceRuleId: Long,
        @Body body: PriceRuleRequest
    ): Response<PriceRuleResponse>

    @PUT("price_rules/{price_rule_id}/discount_codes/{discount_code_id}.json")
    suspend fun updateDiscountCode(
        @Path("price_rule_id") priceRuleId: Long,
        @Path("discount_code_id") discountCodeId: Long,
        @Body body: DiscountCodeRequest
    ): Response<DiscountCodeResponse>

    @DELETE("price_rules/{price_rule_id}/discount_codes/{discount_code_id}.json")
    suspend fun deleteDiscountCode(
        @Path("price_rule_id") priceRuleId: Long,
        @Path("discount_code_id") discountCodeId: Long
    ): Response<Unit>

    @DELETE("price_rules/{price_rule_id}.json")
    suspend fun deletePriceRule(@Path("price_rule_id") priceRuleId: Long): Response<Unit>
}
