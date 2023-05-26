package com.oborodulin.home.servicing.ui.model

import com.oborodulin.home.data.util.MeterType
import com.oborodulin.home.data.util.ServiceType
import java.util.UUID

data class ServiceUi(
    val id: UUID? = null,
    var servicePos: Int? = null,
    val serviceType: ServiceType,
    val serviceMeterType: MeterType = MeterType.NONE,
    var serviceTlId: UUID? = null,
    val serviceName: String = "",
    var serviceMeasureUnit: String? = null,
    val serviceDesc: String? = null
)