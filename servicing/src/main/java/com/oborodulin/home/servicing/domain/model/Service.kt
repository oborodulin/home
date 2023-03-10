package com.oborodulin.home.servicing.domain.model

import com.oborodulin.home.data.util.MeterType
import com.oborodulin.home.data.util.ServiceType
import com.oborodulin.home.domain.model.DomainModel
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*

data class Service(
    var payerServiceId: UUID? = null,
    var serviceTlId: UUID? = null,
    val servicePos: Int,
    val serviceName: String = "",
    val serviceType: ServiceType,
    val meterType: MeterType = MeterType.NONE,
    val serviceMeasureUnit: String? = null,
    val serviceDesc: String? = null,
    var isPrivileges: Boolean? = null,
    var isAllocateRate: Boolean? = null,
    val fromPaymentDate: OffsetDateTime? = null,
    val toPaymentDate: OffsetDateTime? = null,
    val rateValue: BigDecimal? = null,
    val diffMeterValue: BigDecimal? = null,
    val serviceDebt: BigDecimal? = null
) : DomainModel()
