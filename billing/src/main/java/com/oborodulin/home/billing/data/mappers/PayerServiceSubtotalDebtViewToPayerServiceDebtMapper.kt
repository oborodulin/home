package com.oborodulin.home.billing.data.mappers

import com.oborodulin.home.billing.domain.model.PayerServiceSubtotal
import com.oborodulin.home.common.mapping.Mapper
import com.oborodulin.home.data.local.db.views.PayerServiceSubtotalDebtView
import com.oborodulin.home.servicing.domain.model.PayerService
import com.oborodulin.home.servicing.domain.model.Service

class PayerServiceSubtotalDebtViewToPayerServiceDebtMapper :
    Mapper<PayerServiceSubtotalDebtView, PayerServiceSubtotal> {
    override fun map(input: PayerServiceSubtotalDebtView): PayerServiceSubtotal {
        val service = Service(
            servicePos = input.servicePos,
            serviceType = input.serviceType,
            serviceName = input.serviceName
        )
        service.id = input.serviceId
        val payerService = PayerService(
            payerId = input.payerId,
            service = service
        )
        payerService.id = input.payerServiceId
        val payerServiceSubtotal = PayerServiceSubtotal(
            payerService = payerService,
            fromPaymentDate = input.fromPaymentDate,
            toPaymentDate = input.toPaymentDate,
            diffMeterValue = input.diffMeterValue,
            serviceDebt = input.serviceDebt
        )
        payerServiceSubtotal.id = input.payerServiceId
        return payerServiceSubtotal
    }
}