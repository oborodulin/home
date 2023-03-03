package com.oborodulin.home.servicing.ui.model

import com.oborodulin.home.common.ui.model.ListItemModel
import com.oborodulin.home.data.util.ServiceType
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*

data class ServiceSubtotalListItem(
    val id: UUID,
    val name: String = "",
    val type: ServiceType,
    val measureUnit: String? = null,
    val serviceDescr: String? = null,
    var isPrivileges: Boolean? = null,
    var isAllocateRate: Boolean? = null,
    val fromPaymentDate: OffsetDateTime? = null,
    val toPaymentDate: OffsetDateTime? = null,
    val rateValue: BigDecimal? = null,
    val diffMeterValue: BigDecimal? = null,
    val serviceDebt: BigDecimal? = null
) : ListItemModel(
    itemId = id,
    title = name,
    descr = serviceDescr,
    value = serviceDebt,
    fromDate = fromPaymentDate,
    toDate = toPaymentDate
){
    init {

    }
}
