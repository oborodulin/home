package com.oborodulin.home.servicing.domain.model

import com.oborodulin.home.data.util.MeterType
import com.oborodulin.home.data.util.ServiceType
import com.oborodulin.home.common.domain.model.DomainModel
import java.util.UUID

data class Service(
    var serviceTlId: UUID? = null,
    val servicePos: Int?,
    val serviceName: String = "",
    val serviceType: ServiceType,
    val serviceMeterType: MeterType = MeterType.NONE,
    val serviceMeasureUnit: String? = null,
    val serviceDesc: String? = null
) : DomainModel()
