package com.oborodulin.home.servicing.data.mappers

import com.oborodulin.home.common.mapping.Mapper
import com.oborodulin.home.data.local.db.views.PayerServiceSubtotalDebtView
import com.oborodulin.home.servicing.domain.model.Service

class PayerServiceSubtotalDebtViewToServiceMapper : Mapper<PayerServiceSubtotalDebtView, Service> {
    override fun map(input: PayerServiceSubtotalDebtView): Service {
        val service = Service(
            pos = input.pos,
            name = input.name,
            type = input.type,
            measureUnit = input.measureUnit,
            payerServiceId = input.payerServiceId,
            fromPaymentDate = input.fromPaymentDate,
            toPaymentDate = input.toPaymentDate,
            rateValue = input.rateValue,
            diffMeterValue = input.diffMeterValue,
            serviceDebt = input.serviceDebt
        )
        service.id = input.serviceId
        return service
    }
}