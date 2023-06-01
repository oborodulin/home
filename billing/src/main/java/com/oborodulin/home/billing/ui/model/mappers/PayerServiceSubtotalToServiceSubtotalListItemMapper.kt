package com.oborodulin.home.billing.ui.model.mappers

import com.oborodulin.home.billing.domain.model.PayerServiceSubtotal
import com.oborodulin.home.billing.ui.model.ServiceSubtotalListItem
import com.oborodulin.home.common.mapping.Mapper
import java.util.UUID

class PayerServiceSubtotalToServiceSubtotalListItemMapper :
    Mapper<PayerServiceSubtotal, ServiceSubtotalListItem> {
    override fun map(input: PayerServiceSubtotal) = ServiceSubtotalListItem(
        id = input.id ?: UUID.randomUUID(),
        serviceName = input.payerService.service.serviceName,
        serviceType = input.payerService.service.serviceType,
        serviceMeasureUnit = input.payerService.service.serviceMeasureUnit,
        serviceDesc = input.payerService.service.serviceDesc,
        isPrivileges = input.payerService.isPrivileges,
        isAllocateRate = input.payerService.isAllocateRate,
        fromPaymentDate = input.fromPaymentDate,
        toPaymentDate = input.toPaymentDate,
        diffMeterValue = input.diffMeterValue,
        serviceDebt = input.serviceDebt
    )
}