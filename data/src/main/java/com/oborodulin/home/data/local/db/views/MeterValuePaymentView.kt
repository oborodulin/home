package com.oborodulin.home.data.local.db.views

import androidx.room.DatabaseView
import com.oborodulin.home.data.util.Constants
import com.oborodulin.home.data.util.Constants.DB_FALSE
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*

@DatabaseView(
    viewName = MeterValuePaymentView.VIEW_NAME,
    value = """
SELECT cmv.payerId, cmv.payerServiceId, cmv.metersId, cmv.meterValueId, 
    cmv.meterValue AS startMeterValue, nmv.meterValue AS endMeterValue,
    (CASE WHEN nmv.isDerivedUnit = $DB_FALSE 
        THEN CASE WHEN nmv.meterValue >= cmv.meterValue 
                THEN nmv.meterValue - cmv.meterValue 
                ELSE (cmv.maxValue - cmv.meterValue) + nmv.meterValue
            END 
        ELSE nmv.meterValue 
    END) AS diffMeterValue, 
    cmv.isDerivedUnit, cmv.derivedUnit, cmv.measureUnit, 
    cmv.paymentDate AS fromPaymentDate, nmv.paymentDate AS toPaymentDate, 
    (strftime('%Y', ifnull(nmv.paymentDate, datetime('now', 'localtime')), 'start of month', '-1 day') * 12 + 
        strftime('%m', ifnull(nmv.paymentDate, datetime('now', 'localtime')), 'start of month', '-1 day') -
    strftime('%Y', cmv.paymentDate) * 12 - strftime('%m', cmv.paymentDate) +
        (strftime('%d', ifnull(nmv.paymentDate, datetime('now', 'localtime')), '+1 day') = '01' OR 
        strftime('%d', ifnull(nmv.paymentDate, datetime('now', 'localtime'))) >= strftime('%d', cmv.paymentDate))) AS diffMonths,
    nmv.paymentMonth, nmv.paymentYear, cmv.meterLocCode 
FROM ${MeterValuePaymentPeriodView.VIEW_NAME} cmv LEFT JOIN ${MeterValuePaymentPeriodView.VIEW_NAME} nmv
    ON nmv.metersId = cmv.metersId
        AND nmv.payerServiceId = cmv.payerServiceId
        AND nmv.meterLocCode = cmv.meterLocCode
        AND strftime(${Constants.DB_FRACT_SEC_TIME}, nmv.paymentDate) = 
            (SELECT MIN(strftime(${Constants.DB_FRACT_SEC_TIME}, mvp.paymentDate))
                FROM ${MeterValuePaymentPeriodView.VIEW_NAME} mvp
                WHERE mvp.metersId = nmv.metersId
                    AND mvp.payerServiceId = nmv.payerServiceId
                    AND mvp.meterLocCode = nmv.meterLocCode
                    AND strftime(${Constants.DB_FRACT_SEC_TIME}, mvp.paymentDate) > strftime(${Constants.DB_FRACT_SEC_TIME}, cmv.paymentDate))
SELECT mv.payerId, mv.payerServiceId, mv.metersId, mv.meterValueId, 
        mv.startMeterValue, mv.endMeterValue, 
        mv.diffMeterValue / (CASE WHEN mv.diffMonths > 0 THEN mv.diffMonths ELSE 1 END) AS diffMeterValue, 
        mv.isDerivedUnit, mv.derivedUnit, mv.measureUnit, 
        mv.fromPaymentDate, mv.toPaymentDate, 
        mv.diffMonths, mv.paymentMonth, mv.paymentYear, mv.meterLocCode
FROM (SELECT cmv.payerId, cmv.payerServiceId, cmv.metersId, cmv.meterValueId, 
        cmv.meterValue AS startMeterValue, nmv.meterValue AS endMeterValue,
        (CASE WHEN nmv.isDerivedUnit = 0 
            THEN CASE WHEN nmv.meterValue >= cmv.meterValue 
                    THEN nmv.meterValue - cmv.meterValue 
                    ELSE (cmv.maxValue - cmv.meterValue) + nmv.meterValue
                END 
            ELSE nmv.meterValue 
        END) AS diffMeterValue, 
        cmv.isDerivedUnit, cmv.derivedUnit, cmv.measureUnit, 
        cmv.paymentDate AS fromPaymentDate, nmv.paymentDate AS toPaymentDate, 
        (strftime('%Y', ifnull(nmv.paymentDate, datetime('now', 'localtime')), 'start of month', '-1 day') * 12 + 
            strftime('%m', ifnull(nmv.paymentDate, datetime('now', 'localtime')), 'start of month', '-1 day') -
        strftime('%Y', cmv.paymentDate) * 12 - strftime('%m', cmv.paymentDate) +
            (strftime('%d', ifnull(nmv.paymentDate, datetime('now', 'localtime')), '+1 day') = '01' OR 
            strftime('%d', ifnull(nmv.paymentDate, datetime('now', 'localtime'))) >= strftime('%d', cmv.paymentDate))) AS diffMonths,
        nmv.paymentMonth, nmv.paymentYear, cmv.meterLocCode 
    FROM meter_value_payment_periods_view cmv LEFT JOIN meter_value_payment_periods_view nmv
        ON nmv.metersId = cmv.metersId
            AND nmv.payerServiceId = cmv.payerServiceId
            AND nmv.meterLocCode = cmv.meterLocCode
            AND strftime('%Y-%m-%dT%H:%M:%f', nmv.paymentDate) = 
                (SELECT MIN(strftime('%Y-%m-%dT%H:%M:%f', mvp.paymentDate))
                FROM meter_value_payment_periods_view mvp
                WHERE mvp.metersId = nmv.metersId
                    AND mvp.payerServiceId = nmv.payerServiceId
                    AND mvp.meterLocCode = nmv.meterLocCode
                    AND strftime('%Y-%m-%dT%H:%M:%f', mvp.paymentDate) > strftime('%Y-%m-%dT%H:%M:%f', cmv.paymentDate))) mv
WHERE mv.metersId = 'a671af80-c1d8-418b-9b90-be140bd2435c'                                          
"""
)
class MeterValuePaymentView(
    val payerId: UUID,
    val payerServiceId: UUID,
    val metersId: UUID,
    val meterValueId: UUID,
    val startMeterValue: BigDecimal,
    val endMeterValue: BigDecimal?,
    val diffMeterValue: BigDecimal?,
    val isDerivedUnit: Boolean,
    val derivedUnit: String?,
    val measureUnit: String,
    val fromPaymentDate: OffsetDateTime,
    val toPaymentDate: OffsetDateTime?,
    val diffMonths: Int,
    val paymentMonth: Int?,
    val paymentYear: Int?,
    val meterLocCode: String
) {
    companion object {
        const val VIEW_NAME = "meter_value_payments_view"
    }
}