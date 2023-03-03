package com.oborodulin.home.servicing.data.mappers

import com.oborodulin.home.common.mapping.Mapper
import com.oborodulin.home.data.local.db.views.PayerTotalDebtView
import com.oborodulin.home.domain.model.Payer

class PayerTotalDebtViewToPayerMapper : Mapper<PayerTotalDebtView, Payer> {
    override fun map(input: PayerTotalDebtView): Payer {
        val payer = Payer(
            fromPaymentDate = input.fromPaymentDate,
            toPaymentDate = input.toPaymentDate,
            totalDebt = input.totalDebt
        )
        payer.id = input.payerId
        return payer
    }
}