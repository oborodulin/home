package com.oborodulin.home.data.local.db.views

import androidx.room.DatabaseView
import androidx.room.Embedded
import com.oborodulin.home.data.local.db.entities.PayerServiceCrossRefEntity
import com.oborodulin.home.data.local.db.entities.PayerServiceMeterCrossRefEntity
import java.util.*

@DatabaseView(
    viewName = MeterPayerServiceView.VIEW_NAME,
    value = """
SELECT m.*, s.*, ps.payerServiceId, ps.isPrivileges, ps.isAllocateRate
FROM ${MeterView.VIEW_NAME} m JOIN ${PayerServiceMeterCrossRefEntity.TABLE_NAME} psm ON psm.metersId = m.meterId
    JOIN ${PayerServiceCrossRefEntity.TABLE_NAME} ps ON ps.payerServiceId = psm.payersServicesId
    JOIN ${ServiceView.VIEW_NAME} s ON s.serviceId = ps.servicesId AND s.serviceLocCode = m.meterLocCode 
ORDER BY ps.payersId, s.servicePos
"""
)
class MeterPayerServiceView(
    @Embedded
    val meter: MeterView,
    @Embedded
    val service: ServiceView,
    val payerServiceId: UUID,
    val isPrivileges: Boolean = false,
    val isAllocateRate: Boolean = false
) {
    companion object {
        const val VIEW_NAME = "meter_payer_services_view"
    }
}