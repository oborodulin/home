package com.oborodulin.home.data.local.db.views

import androidx.room.*
import com.oborodulin.home.data.local.db.entities.ServiceEntity
import com.oborodulin.home.data.local.db.entities.ServiceTlEntity

@DatabaseView(
    viewName = ServiceView.VIEW_NAME,
    value = """
SELECT s.*, stl.* FROM ${ServiceEntity.TABLE_NAME} s JOIN ${ServiceTlEntity.TABLE_NAME} stl ON stl.servicesId = s.serviceId
ORDER BY s.servicePos
"""
)
class ServiceView(
    @Embedded
    val data: ServiceEntity,
    @Embedded
    val tl: ServiceTlEntity,
) {
    companion object {
        const val VIEW_NAME = "services_view"
    }
}