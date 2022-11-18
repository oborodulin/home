package com.oborodulin.home.data.local.db.views

import androidx.room.DatabaseView
import androidx.room.Embedded
import com.oborodulin.home.data.local.db.entities.MeterEntity
import com.oborodulin.home.data.local.db.entities.MeterTlEntity
import com.oborodulin.home.data.local.db.entities.PayerServiceCrossRefEntity

@DatabaseView(
    viewName = MeterView.VIEW_NAME,
    value = "SELECT m.*, mtl.*, ps.* FROM meters AS m JOIN meters_tl AS mtl ON mtl.metersId = m.meterId " +
            "JOIN payers_services AS ps ON ps.payerServiceId = m.payersServicesId " +
            "JOIN services AS s ON s.serviceId = ps.servicesId " +
            "ORDER BY ps.payersId, s.pos"
)
class MeterView(
    @Embedded
    var data: MeterEntity,
    @Embedded
    var tl: MeterTlEntity,
    @Embedded
    var ps: PayerServiceCrossRefEntity
) {
    companion object {
        const val VIEW_NAME = "meters_view"
    }
}