package com.oborodulin.home.data.local.db.entities.pojo

import com.oborodulin.home.data.util.ServiceType
import java.util.*

data class ServicePojo(
    var id: UUID,
    var pos: Int?,
    var type: ServiceType,
    val name: String,
    var measureUnit: String?,
    val descr: String?,
)
