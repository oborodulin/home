package com.oborodulin.home.data.local.db.views

import androidx.room.DatabaseView
import androidx.room.Embedded
import com.oborodulin.home.data.local.db.entities.*
import java.util.*

@DatabaseView(
    viewName = MeterView.VIEW_NAME,
    value = """
SELECT m.*, mtl.*, ps.payerServiceId, ps.servicesId,
    (CASE WHEN instr(mtl.measureUnit, '/') = 0 THEN 0 ELSE 1 END) AS isDerivedUnit, 
    (CASE WHEN instr(mtl.measureUnit, '/') <> 0 
        THEN substr(mtl.measureUnit, instr(mtl.measureUnit, '/') + 1) 
        ELSE NULL 
    END) AS derivedUnit
FROM ${MeterEntity.TABLE_NAME} m JOIN ${MeterTlEntity.TABLE_NAME} mtl ON mtl.metersId = m.meterId
    JOIN ${PayerServiceMeterCrossRefEntity.TABLE_NAME} psm ON psm.metersId = m.meterId
    JOIN ${PayerServiceCrossRefEntity.TABLE_NAME} ps ON ps.payerServiceId = psm.payersServicesId
    JOIN ${ServiceEntity.TABLE_NAME} s ON s.serviceId = ps.servicesId
ORDER BY ps.payersId, s.servicePos
"""
)
class MeterView(
    @Embedded
    val data: MeterEntity,
    @Embedded
    val tl: MeterTlEntity,
    val payerServiceId: UUID,
    val servicesId: UUID,
    val isDerivedUnit: Boolean,
    val derivedUnit: String
) {
    companion object {
        const val VIEW_NAME = "meters_view"
    }
}