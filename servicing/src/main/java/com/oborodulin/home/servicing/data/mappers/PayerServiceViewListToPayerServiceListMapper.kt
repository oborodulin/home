package com.oborodulin.home.servicing.data.mappers

import com.oborodulin.home.common.mapping.ListMapper
import com.oborodulin.home.data.local.db.views.PayerServiceView
import com.oborodulin.home.servicing.domain.model.PayerService1

class PayerServiceViewListToPayerServiceListMapper(private val mapper: PayerServiceViewToServiceMapper) :
    ListMapper<PayerServiceView, PayerService1> {
    override fun map(input: List<PayerServiceView>): List<PayerService1> {
        val payerService1s = mutableListOf<PayerService1>()
        input.forEach { psv ->
            val payerService1 = payerService1s.firstOrNull { ps -> ps.payer.id == psv.payersId } ?: PayerService1()
            if (payerService1.payer.id != psv.payersId) {
                payerService1.payer.id = psv.payersId
                payerService1s.add(payerService1)
            }
            payerService1.services.add(mapper.map(psv))
        }
        return payerService1s
    }
}