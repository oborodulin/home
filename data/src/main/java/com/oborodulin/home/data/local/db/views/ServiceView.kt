package com.oborodulin.home.data.local.db.views

import androidx.room.*
import com.oborodulin.home.data.local.db.converters.DateTypeConverter
import com.oborodulin.home.data.local.db.entities.MeterEntity
import com.oborodulin.home.data.local.db.entities.MeterTlEntity
import com.oborodulin.home.data.local.db.entities.ServiceEntity
import com.oborodulin.home.data.local.db.entities.ServiceTlEntity
import java.math.BigDecimal
import java.util.*

@DatabaseView(
    viewName = ServiceView.VIEW_NAME,
    value = "SELECT s.*, stl.* FROM services AS s JOIN services_tl AS stl ON stl.servicesId = s.serviceId"
)
class ServiceView(
    @Embedded
    var data: ServiceEntity,
    @Embedded
    var tl: ServiceTlEntity,
) {
    companion object {
        const val VIEW_NAME = "services_view"
    }
}