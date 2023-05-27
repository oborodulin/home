package com.oborodulin.home.servicing.ui.model.mappers

import com.oborodulin.home.common.mapping.Mapper
import com.oborodulin.home.servicing.domain.model.Service
import com.oborodulin.home.servicing.ui.model.PayerServiceUi

class ServiceToPayerServiceUiMapper : Mapper<Service, PayerServiceUi> {
    override fun map(input: Service) =
        PayerServiceUi(
            id = input.payerServiceId,
            payerId = input.payerId!!,
            serviceId = input.id!!,
            serviceType = input.serviceType,
            serviceMeterType = input.serviceMeterType,
            serviceName = input.serviceName,
            serviceMeasureUnit = input.serviceMeasureUnit,
            serviceDesc = input.serviceDesc,
            fromMonth = input.fromMonth,
            fromYear = input.fromYear,
            periodFromDate = input.periodFromDate,
            periodToDate = input.periodToDate,
            isMeterOwner = input.isMeterOwner ?: false,
            isPrivileges = input.isPrivileges ?: false,
            isAllocateRate = input.isAllocateRate ?: false
        )
}