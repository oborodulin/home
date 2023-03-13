package com.oborodulin.home.data.local.db.views

import androidx.room.DatabaseView
import com.oborodulin.home.data.util.Constants
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*

@DatabaseView(
    viewName = MeterValuePaymentView.VIEW_NAME,
    value = """
SELECT lmv.payerId, lmv.payerServiceId, lmv.metersId, lmv.meterValueId, 
    pmv.meterValue AS startMeterValue, lmv.meterValue AS endMeterValue,
    (CASE WHEN lmv.isDerivedUnit = 0 
        THEN CASE WHEN lmv.meterValue >= pmv.meterValue 
                THEN lmv.meterValue - pmv.meterValue 
                ELSE (pmv.maxValue - pmv.meterValue) + lmv.meterValue + (1 - (pmv.maxValue - cast(pmv.maxValue as int ))) 
            END 
        ELSE lmv.meterValue 
    END) AS diffMeterValue, 
    lmv.isDerivedUnit, lmv.derivedUnit, lmv.measureUnit, 
    lmv.paymentDate, 
    lmv.paymentMonth, lmv.paymentYear, lmv.meterLocCode 
FROM ${MeterValuePaymentPeriodView.VIEW_NAME} lmv 
    JOIN ${MeterValuePaymentPeriodView.VIEW_NAME} pmv ON pmv.metersId = lmv.metersId 
        AND pmv.payerServiceId = lmv.payerServiceId
        AND strftime(${Constants.DB_FRACT_SEC_TIME}, pmv.paymentDate) = strftime(${Constants.DB_FRACT_SEC_TIME}, lmv.paymentDate, '-1 months')
        AND pmv.meterLocCode = lmv.meterLocCode
"""
)
class MeterValuePaymentView(
    val payerId: UUID,
    val payerServiceId: UUID,
    val metersId: UUID,
    val meterValueId: UUID,
    val startMeterValue: BigDecimal,
    val endMeterValue: BigDecimal,
    val diffMeterValue: BigDecimal,
    val isDerivedUnit: Boolean,
    val derivedUnit: String,
    val measureUnit: String,
    val paymentDate: OffsetDateTime,
    val paymentMonth: Int,
    val paymentYear: Int,
    val meterLocCode: String
) {
    companion object {
        const val VIEW_NAME = "meter_value_payments_view"
    }
}