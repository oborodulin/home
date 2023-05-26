package com.oborodulin.home.servicing.ui.model

import com.oborodulin.home.common.ui.model.ListItemModel
import com.oborodulin.home.data.util.MeterType
import com.oborodulin.home.data.util.ServiceType
import java.time.OffsetDateTime
import java.util.UUID

data class PayerServiceListItem(
    val id: UUID,
    var servicePos: Int? = null,
    val serviceType: ServiceType,
    val serviceMeterType: MeterType = MeterType.NONE,
    val serviceName: String = "",
    var serviceMeasureUnit: String? = null,
    val serviceDesc: String? = null,
    val fromMonth: Int? = null, // if null then from meters data values
    val fromYear: Int? = null,
    val periodFromDate: OffsetDateTime? = null, // period in year for heating
    val periodToDate: OffsetDateTime? = null,
    val isMeterOwner: Boolean = false,
    val isPrivileges: Boolean = false,
    val isAllocateRate: Boolean = false
) : ListItemModel(
    itemId = id,
    title = serviceName,
    descr = serviceDesc
)
