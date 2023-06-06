package com.oborodulin.home.servicing.data.mappers

import com.oborodulin.home.common.mapping.ListMapperImpl
import com.oborodulin.home.data.local.db.views.PayerServiceView
import com.oborodulin.home.servicing.domain.model.PayerService

class PayerServiceViewListToPayerServiceListMapper(val mapper: PayerServiceViewToPayerServiceMapper) :
    ListMapperImpl<PayerServiceView, PayerService>(mapper)
/*{
    override fun map(input: List<PayerServiceView>): List<PayerService> {
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

 */