package com.example.m_commerce_admin.features.coupons.data.model

import com.google.gson.annotations.SerializedName

data class DiscountCodeResponse(
    @SerializedName("discount_code")
    val discount_code: DiscountCodeResult? = null,
    @SerializedName("discount_codes")
    val discount_codes: List<DiscountCodeResult>? = null
)

data class DiscountCodeResult(
    val id: Long,
    val code: String,
    val usage_count: Int
)
