package com.example.m_commerce_admin.features.coupons.data.model

import com.example.m_commerce_admin.GetCouponsQuery
import com.example.m_commerce_admin.features.coupons.domain.entity.CouponItem
fun GetCouponsQuery.Data.toCouponItems(): List<CouponItem> {
    return discountNodes.edges.mapNotNull { edge ->
        val discount = edge.node.discount.onDiscountCodeBasic
        val codeNode = discount?.codes?.edges?.firstOrNull()?.node
        val code = codeNode?.code
        val usedCount = codeNode?.asyncUsageCount
        val value = discount?.customerGets?.value?.onDiscountPercentage?.percentage

        val rawAmount = discount?.customerGets?.value?.onDiscountAmount?.amount?.amount
        val amount: Double = when (rawAmount) {
            is Double -> rawAmount
            is Number -> rawAmount.toDouble()
            is String -> rawAmount.toDoubleOrNull() ?: 0.0
            else -> 0.0
        }

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
                amount = amount,
                currencyCode = currencyCode?.name ?: "USD"
            )
        } else {
            null
        }
    }
}
