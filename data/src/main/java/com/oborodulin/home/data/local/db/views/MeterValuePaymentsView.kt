package com.oborodulin.home.data.local.db.views

import androidx.room.DatabaseView
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*

@DatabaseView(
    viewName = MeterValuePaymentsView.VIEW_NAME,
    value = """
SELECT lmv.payerId, lmv.payerServiceId, lmv.metersId, pmv.meterValue AS startMeterValue, lmv.meterValue AS endMeterValue,
    (CASE WHEN instr(lmv.measureUnit, '/') = 0 THEN 0 ELSE 1 END) AS isDerivedUnit, 
    (CASE WHEN instr(lmv.measureUnit, '/') <> 0 
        THEN substr(lmv.measureUnit, instr(lmv.measureUnit, '/') + 1) ELSE NULL END) AS derivedUnit, 
    (CASE WHEN instr(lmv.measureUnit, '/') = 0 THEN (lmv.meterValue - pmv.meterValue) ELSE lmv.meterValue END) AS diffMeterValue, 
        lmv.measureUnit, lmv.paymentDate, lmv.paymentMonth, lmv.paymentYear 
FROM meter_value_payment_periods_view lmv 
    JOIN meter_value_payment_periods_view pmv ON pmv.metersId = lmv.metersId 
        AND pmv.paymentDate = datetime(lmv.paymentDate, '-1 months')
"""
)
class MeterValuePaymentsView(
    val payerId: UUID,
    val payerServiceId: UUID,
    val metersId: UUID,
    val startMeterValue: BigDecimal,
    val endMeterValue: BigDecimal,
    val isDerivedUnit: Boolean,
    val derivedUnit: String,
    val diffMeterValue: BigDecimal,
    val measureUnit: String,
    val paymentDate: OffsetDateTime,
    val paymentMonth: Int,
    val paymentYear: Int
) {
    companion object {
        const val VIEW_NAME = "meter_value_payments_view"
    }
}