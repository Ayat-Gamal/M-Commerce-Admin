package com.example.m_commerce_admin.features.coupons.domain.entity

data class CouponItem(
    val id: String? = null,

    val code: String,
    val value: Double?,
    val usedCount: Int?,
    val startsAt: String?,
    val endsAt: String?,
    val title: String?,
    val summary: String?,
    val usageLimit: Int?,
    val createdAt: String?,
    val updatedAt: String?,
    val amount: Double,
    val currencyCode: String?
)


fun CouponItem.toCouponInput(): CouponInput {
    return CouponInput(
        id = this.id,
        title = this.title,
        summary = this.summary,
        code = this.code,
        startsAt = this.startsAt,
        endsAt = this.endsAt,
        usageLimit = this.usageLimit,
        discountValue = this.value ?: this.amount,
        discountType = if (this.value != null) DiscountType.PERCENTAGE else DiscountType.FIXED_AMOUNT,
        currencyCode = this.currencyCode,
        appliesOncePerCustomer = true
    )
}
