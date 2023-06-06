package com.oborodulin.home.servicing.data.mappers

import com.oborodulin.home.common.mapping.Mapper
import com.oborodulin.home.data.local.db.views.PayerServiceView
import com.oborodulin.home.servicing.domain.model.PayerService

class PayerServiceViewToPayerServiceMapper(private val mapper: ServiceViewToServiceMapper) :
    Mapper<PayerServiceView, PayerService> {
    override fun map(input: PayerServiceView): PayerService {
        val payerService = PayerService(
            payerId = input.payersId,
            service = mapper.map(input.service),
            fromMonth = input.fromMonth,
            fromYear = input.fromYear,
            periodFromDate = input.periodFromDate,
            periodToDate = input.periodToDate,
            isMeterOwner = input.isMeterOwner,
            isPrivileges = input.isPrivileges,
            isAllocateRate = input.isAllocateRate
        )
        payerService.id = input.payerServiceId
        return payerService
    }
}