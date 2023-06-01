package com.oborodulin.home.servicing.domain.model

import com.oborodulin.home.common.domain.model.DomainModel
import java.time.OffsetDateTime
import java.util.UUID

data class PayerService(
    val payerId: UUID,
    val service: Service,
    val fromMonth: Int? = null,
    val fromYear: Int? = null,
    val periodFromDate: OffsetDateTime? = null,
    val periodToDate: OffsetDateTime? = null,
    val isMeterOwner: Boolean? = null,
    var isPrivileges: Boolean? = null,
    var isAllocateRate: Boolean? = null,
) : DomainModel()
