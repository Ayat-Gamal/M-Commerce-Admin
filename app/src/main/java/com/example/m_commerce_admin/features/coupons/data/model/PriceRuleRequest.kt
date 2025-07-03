package com.example.m_commerce_admin.features.coupons.data.model

data class PriceRuleRequest(
    val price_rule: PriceRule
)

data class PriceRule(
    val title: String,
    val target_type: String = "line_item",
    val target_selection: String = "all",
    val allocation_method: String = "across",
    val value_type: String = "fixed_amount",
    val value: String,
    val customer_selection: String = "all",
    val starts_at: String,
    val ends_at: String,
    val usage_limit: Int
)
