package com.oborodulin.home.servicing.data.mappers

import com.oborodulin.home.common.mapping.ListMapper
import com.oborodulin.home.data.local.db.views.PayerServiceView
import com.oborodulin.home.domain.model.Payer
import com.oborodulin.home.servicing.domain.model.PayerService

class PayerServiceViewListToPayerServiceListMapper(private val mapper: PayerServiceViewToServiceMapper) :
    ListMapper<PayerServiceView, PayerService> {
    override fun map(input: List<PayerServiceView>): List<PayerService> {
        val payerServices = mutableListOf<PayerService>()
        input.forEach { psv ->
            val payerService = payerServices.firstOrNull { ps -> ps.payer.id == psv.payersId } ?: PayerService()
            if (payerService.payer.id != psv.payersId) {
                payerService.payer.id = psv.payersId
                payerServices.add(payerService)
            }
            payerService.services.add(mapper.map(psv))
        }
        return payerServices
    }
}