package com.oborodulin.home.data.local.db.views

import androidx.room.DatabaseView
import androidx.room.Embedded
import com.oborodulin.home.data.local.db.entities.MeterEntity
import com.oborodulin.home.data.local.db.entities.MeterTlEntity

@DatabaseView(
    viewName = MeterView.VIEW_NAME,
    value = """
SELECT m.*, mtl.*,
    (CASE WHEN instr(mtl.meterMeasureUnit, '/') = 0 THEN 0 ELSE 1 END) AS isDerivedUnit, 
    (CASE WHEN instr(mtl.meterMeasureUnit, '/') <> 0 
        THEN substr(mtl.meterMeasureUnit, instr(mtl.meterMeasureUnit, '/') + 1) 
        ELSE NULL 
    END) AS derivedUnit
FROM ${MeterEntity.TABLE_NAME} m JOIN ${MeterTlEntity.TABLE_NAME} mtl ON mtl.metersId = m.meterId
"""
)
class MeterView(
    @Embedded
    val data: MeterEntity,
    @Embedded
    val tl: MeterTlEntity,
    val isDerivedUnit: Boolean,
    val derivedUnit: String?
) {
    companion object {
        const val VIEW_NAME = "meters_view"
    }
}