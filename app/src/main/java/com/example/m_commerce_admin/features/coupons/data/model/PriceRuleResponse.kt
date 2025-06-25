package com.example.m_commerce_admin.features.coupons.data.model

import com.google.gson.annotations.SerializedName

data class PriceRuleResponse(
    @SerializedName("price_rule")
    val price_rule: PriceRuleResult
)

data class PriceRuleResult(
    val id: Long,
    val title: String,
    val value: String,
    val value_type: String,
    val usage_limit: Int,
    val starts_at: String,
    val ends_at: String
)
