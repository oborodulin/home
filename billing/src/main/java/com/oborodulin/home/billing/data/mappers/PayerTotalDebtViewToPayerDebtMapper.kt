package com.oborodulin.home.billing.data.mappers

import com.oborodulin.home.billing.domain.model.PayerDebt
import com.oborodulin.home.common.mapping.Mapper
import com.oborodulin.home.data.local.db.views.PayerTotalDebtView

class PayerTotalDebtViewToPayerDebtMapper : Mapper<PayerTotalDebtView, PayerDebt> {
    override fun map(input: PayerTotalDebtView): PayerDebt {
        val payerDebt = PayerDebt(
            fromPaymentDate = input.fromPaymentDate,
            toPaymentDate = input.toPaymentDate,
            totalDebt = input.totalDebt
        )
        payerDebt.id = input.payerId
        return payerDebt
    }
}