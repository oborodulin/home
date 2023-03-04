package com.oborodulin.home.servicing.domain.model

import com.oborodulin.home.data.util.ServiceType
import com.oborodulin.home.domain.model.DomainModel
import com.oborodulin.home.domain.model.Payer
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*

data class Service(
    val payer: Payer = Payer(),
    var serviceTlId: UUID? = null,
    var payerServiceId: UUID? = null,
    var servicePos: Int,
    val serviceName: String = "",
    val serviceType: ServiceType,
    val measureUnit: String?,
    val serviceDesc: String? = null,
    var isPrivileges: Boolean? = null,
    var isAllocateRate: Boolean? = null,
    val fromPaymentDate: OffsetDateTime? = null,
    val toPaymentDate: OffsetDateTime? = null,
    val rateValue: BigDecimal? = null,
    val diffMeterValue: BigDecimal? = null,
    val serviceDebt: BigDecimal? = null
) : DomainModel()
