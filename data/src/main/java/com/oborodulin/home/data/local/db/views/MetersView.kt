package com.oborodulin.home.data.local.db.views

import androidx.room.DatabaseView
import androidx.room.Embedded
import com.oborodulin.home.data.local.db.entities.MeterEntity
import com.oborodulin.home.data.local.db.entities.MeterTlEntity
import com.oborodulin.home.data.local.db.entities.PayerServiceCrossRefEntity
import java.util.*

@DatabaseView(
    viewName = MetersView.VIEW_NAME,
    value = """
SELECT m.*, mtl.*, ps.payerServiceId, ps.servicesId
FROM meters AS m JOIN meters_tl AS mtl ON mtl.metersId = m.meterId
    JOIN payers_services_meters AS psm ON psm.metersId = m.meterId
    JOIN payers_services AS ps ON ps.payerServiceId = psm.payersServicesId
    JOIN services AS s ON s.serviceId = ps.servicesId
ORDER BY ps.payersId, s.pos
"""
)
class MetersView(
    @Embedded
    val data: MeterEntity,
    @Embedded
    val tl: MeterTlEntity,
    val payerServiceId: UUID,
    val servicesId: UUID
) {
    companion object {
        const val VIEW_NAME = "meters_view"
    }
}