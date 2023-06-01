package com.oborodulin.home.billing.ui.model

import com.oborodulin.home.common.ui.model.ListItemModel
import com.oborodulin.home.data.util.ServiceType
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID

data class ServiceSubtotalListItem(
    val id: UUID,
    val serviceName: String = "",
    val serviceType: ServiceType,
    val serviceMeasureUnit: String? = null,
    val serviceDesc: String? = null,
    var isPrivileges: Boolean? = null,
    var isAllocateRate: Boolean? = null,
    val fromPaymentDate: OffsetDateTime? = null,
    val toPaymentDate: OffsetDateTime? = null,
    val diffMeterValue: BigDecimal? = null,
    val serviceDebt: BigDecimal? = null
) : ListItemModel(
    itemId = id,
    title = serviceName,
    descr = serviceDesc,
    value = serviceDebt,
    fromDate = fromPaymentDate,
    toDate = toPaymentDate
) {
    init {

    }
}
