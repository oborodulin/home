package com.oborodulin.home.servicing.data.mappers

import com.oborodulin.home.common.mapping.Mapper
import com.oborodulin.home.data.local.db.views.PayerServiceView
import com.oborodulin.home.servicing.domain.model.Service

class PayerServiceViewToServiceMapper(private val mapper: ServiceViewToServiceMapper) :
    Mapper<PayerServiceView, Service> {
    override fun map(input: PayerServiceView): Service {
        val service = mapper.map(input.service)
        with(service) {
            payerServiceId = input.payerServiceId
            isPrivileges = input.isPrivileges
            isAllocateRate = input.isAllocateRate
        }
        return service
    }
}