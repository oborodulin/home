package com.oborodulin.home.data.local.db.entities.pojo

import com.oborodulin.home.data.util.ServiceType
import java.math.BigDecimal
import java.util.*

class PrevServiceMeterValuePojo(
    var type: ServiceType,
    val name: String,
    var measureUnit: String?,
    val prevLastDate: Date,
    val prevValue: BigDecimal,
)

