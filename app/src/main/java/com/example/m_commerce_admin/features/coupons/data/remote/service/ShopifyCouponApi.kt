package com.example.m_commerce_admin.features.coupons.data.remote.service

import com.example.m_commerce_admin.features.coupons.data.model.DiscountCodeRequest
import com.example.m_commerce_admin.features.coupons.data.model.DiscountCodeResponse
import com.example.m_commerce_admin.features.coupons.data.model.PriceRuleRequest
import com.example.m_commerce_admin.features.coupons.data.model.PriceRuleResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface ShopifyCouponApi {
    @POST("price_rules.json")
    suspend fun createPriceRule(@Body body: PriceRuleRequest): Response<PriceRuleResponse>

    @POST("price_rules/{price_rule_id}/discount_codes.json")
    suspend fun createDiscountCode(
        @Path("price_rule_id") priceRuleId: Long,
        @Body body: DiscountCodeRequest
    ): Response<DiscountCodeResponse>
}
