package com.example.m_commerce_admin.features.coupons.data

import com.example.m_commerce_admin.GetCouponsQuery
import com.example.m_commerce_admin.features.coupons.domain.entity.CouponItem

fun GetCouponsQuery.Data.toCouponItems(): List<CouponItem> {
    return discountNodes?.edges?.mapNotNull { edge ->
        val discount = edge?.node?.discount?.onDiscountCodeBasic
        val codeNode = discount?.codes?.edges?.firstOrNull()?.node
        val code = codeNode?.code
        val usedCount = codeNode?.asyncUsageCount
        val value = discount?.customerGets?.value?.onDiscountPercentage?.percentage
        val amount = discount?.customerGets?.value?.onDiscountAmount?.amount?.amount
        val currencyCode = discount?.customerGets?.value?.onDiscountAmount?.amount?.currencyCode
        if (code != null) {
            CouponItem(
                code = code,
                value = value,
                usedCount = usedCount,
                startsAt = discount.startsAt.toString(),
                endsAt = discount.endsAt.toString(),
                title = discount.title,
                summary = discount.summary,
                usageLimit = discount.usageLimit,
                createdAt = discount.createdAt.toString(),
                updatedAt = discount.updatedAt.toString(),
                amount = amount as Double?,
                currencyCode = currencyCode.toString()
            )
        } else {
            null
        }
    } ?: emptyList()
} 