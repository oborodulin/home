package com.oborodulin.home.servicing.data.mappers

import com.oborodulin.home.common.mapping.Mapper
import com.oborodulin.home.data.local.db.entities.PayerServiceCrossRefEntity
import com.oborodulin.home.servicing.domain.model.PayerService
import java.util.UUID

class PayerServiceToPayerServiceCrossRefEntityMapper :
    Mapper<PayerService, PayerServiceCrossRefEntity> {
    override fun map(input: PayerService) = PayerServiceCrossRefEntity(
        payerServiceId = input.id ?: input.apply { id = UUID.randomUUID() }.id!!,
        fromMonth = input.fromMonth,
        fromYear = input.fromYear,
        periodFromDate = input.periodFromDate,
        periodToDate = input.periodToDate,
        isMeterOwner = input.isMeterOwner,
        isPrivileges = input.isPrivileges,
        isAllocateRate = input.isAllocateRate,
        payersId = input.payerId,
        servicesId = input.service.id!!,
    )
}