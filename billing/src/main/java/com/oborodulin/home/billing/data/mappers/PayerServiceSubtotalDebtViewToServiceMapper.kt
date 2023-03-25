package com.oborodulin.home.billing.data.mappers

import com.oborodulin.home.common.mapping.Mapper
import com.oborodulin.home.data.local.db.views.PayerServiceSubtotalDebtView
import com.oborodulin.home.servicing.domain.model.Service

class PayerServiceSubtotalDebtViewToServiceMapper : Mapper<PayerServiceSubtotalDebtView, Service> {
    override fun map(input: PayerServiceSubtotalDebtView): Service {
        val service = Service(
            servicePos = input.servicePos,
            serviceName = input.serviceName,
            serviceType = input.serviceType,
            serviceMeasureUnit = input.measureUnit,
            payerServiceId = input.payerServiceId,
            fromPaymentDate = input.fromPaymentDate,
            toPaymentDate = input.toPaymentDate,
            diffMeterValue = input.diffMeterValue,
            serviceDebt = input.serviceDebt
        )
        service.id = input.serviceId
        return service
    }
}