package com.oborodulin.home.servicing.ui.model

import com.oborodulin.home.common.ui.model.ListItemModel
import com.oborodulin.home.data.util.MeterType
import com.oborodulin.home.data.util.ServiceType
import java.util.UUID

data class ServiceListItem(
    val id: UUID,
    var servicePos: Int? = null,
    val serviceType: ServiceType,
    val serviceMeterType: MeterType = MeterType.NONE,
    val serviceName: String = "",
    var serviceMeasureUnit: String? = null,
    val serviceDesc: String? = null
) : ListItemModel(
    itemId = id,
    title = serviceName,
    descr = serviceDesc
)
