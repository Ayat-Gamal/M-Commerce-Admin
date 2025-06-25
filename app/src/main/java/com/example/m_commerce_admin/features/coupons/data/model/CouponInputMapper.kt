import com.apollographql.apollo.api.Optional
import com.example.m_commerce_admin.features.coupons.domain.entity.CouponInput
import com.example.m_commerce_admin.features.coupons.domain.entity.DiscountType
import com.example.m_commerce_admin.type.*

fun CouponInput.toGraphQL(): DiscountCodeBasicInput {
    val customerGetsValueInput = when (this.discountType) {
        DiscountType.PERCENTAGE -> {
            DiscountCustomerGetsValueInput(
                percentage = Optional.present(this.discountValue),
            )
        }
        DiscountType.FIXED_AMOUNT -> {
            DiscountCustomerGetsValueInput(

                 discountAmount = Optional.present(DiscountAmountInput(amount = Optional.present(this.discountValue))),
                  )

        }
    }

    val customerGetsInput = DiscountCustomerGetsInput(
        value = Optional.present(customerGetsValueInput),
        items = Optional.absent()
    )
    val customerSelectionInput = DiscountCustomerSelectionInput(
        all = Optional.present(this.appliesOncePerCustomer),
        customerSegments = Optional.absent()
    )

    return DiscountCodeBasicInput(
        title = Optional.presentIfNotNull(this.title),
         code = Optional.presentIfNotNull(this.code),
        startsAt = Optional.presentIfNotNull(this.startsAt),
        endsAt = Optional.presentIfNotNull(this.endsAt),
        usageLimit = Optional.presentIfNotNull(this.usageLimit?.toInt()),
        appliesOncePerCustomer = Optional.presentIfNotNull(this.appliesOncePerCustomer),
        customerGets = Optional.present(customerGetsInput),
        customerSelection = Optional.present(customerSelectionInput)

    )
}
