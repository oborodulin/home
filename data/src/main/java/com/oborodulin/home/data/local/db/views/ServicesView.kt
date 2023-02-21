package com.oborodulin.home.data.local.db.views

import androidx.room.*
import com.oborodulin.home.data.local.db.entities.ServiceEntity
import com.oborodulin.home.data.local.db.entities.ServiceTlEntity

@DatabaseView(
    viewName = ServicesView.VIEW_NAME,
    value = """
SELECT s.*, stl.* FROM services AS s JOIN services_tl AS stl ON stl.servicesId = s.serviceId
ORDER BY s.pos
"""
)
class ServicesView(
    @Embedded
    val data: ServiceEntity,
    @Embedded
    val tl: ServiceTlEntity,
) {
    companion object {
        const val VIEW_NAME = "services_view"
    }
}