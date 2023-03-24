package com.oborodulin.home.data.local.db.views

import androidx.room.DatabaseView
import androidx.room.TypeConverters
import com.oborodulin.home.data.local.db.converters.DateTypeConverter
import com.oborodulin.home.data.util.Constants
import com.oborodulin.home.data.util.ServiceType
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*

@DatabaseView(
    viewName = PayerServiceSubtotalDebtView.VIEW_NAME,
    value = """
SELECT ps.payerId, 
        STRFTIME(${Constants.DB_FRACT_SEC_TIME}, DATETIME(ps.fromPaymentDate, 'localtime')) || 
            PRINTF('%+.2d:%.2d', ROUND((JULIANDAY(ps.fromPaymentDate, 'localtime') - JULIANDAY(ps.fromPaymentDate)) * 24), 
                ABS(ROUND((JULIANDAY(ps.fromPaymentDate, 'localtime') - JULIANDAY(ps.fromPaymentDate)) * 24 * 60) % 60)) AS fromPaymentDate, 
        STRFTIME(${Constants.DB_FRACT_SEC_TIME}, DATETIME(ps.toPaymentDate, 'localtime')) || 
            PRINTF('%+.2d:%.2d', ROUND((JULIANDAY(ps.toPaymentDate, 'localtime') - JULIANDAY(ps.toPaymentDate)) * 24), 
                ABS(ROUND((JULIANDAY(ps.toPaymentDate, 'localtime') - JULIANDAY(ps.toPaymentDate)) * 24 * 60) % 60)) AS toPaymentDate, 
        ps.serviceId, ps.payerServiceId, ps.fullMonths,
        ps.servicePos, ps.serviceType, ps.serviceName, ps.serviceLocCode, ps.rateValue, 
        ps.fromMeterValue, ps.toMeterValue, ps.diffMeterValue, ps.measureUnit, 
        ps.serviceDebt, ps.isMeterUses
FROM (SELECT psc.payerId, 
        (CASE WHEN psc.isMeterUses = 0 AND psc.fromServiceDate IS NOT NULL 
            THEN CASE WHEN julianday(psc.fromServiceDate) - julianday(psc.rateStartDate) > 0
                    THEN psc.fromServiceDate
                    ELSE psc.rateStartDate
                END
            ELSE MIN(strftime(${Constants.DB_FRACT_SEC_TIME}, psc.paymentDate))
        END) AS fromPaymentDate, 
        (CASE WHEN psc.isMeterUses = 0 AND psc.fromServiceDate IS NOT NULL 
            THEN ifnull(psl.rateStartDate, datetime('now', 'local'))
            ELSE MAX(strftime(${Constants.DB_FRACT_SEC_TIME}, psc.paymentDate))
        END) AS toPaymentDate, 
        psc.serviceId, psc.payerServiceId, psc.fromServiceDate, 
        (CASE WHEN psc.isMeterUses = 0 AND psc.fromServiceDate IS NOT NULL 
            THEN (CASE 
                    WHEN julianday(psc.fromServiceDate) - julianday(psc.rateStartDate) > 0
                    THEN strftime('%Y', ifnull(psl.rateStartDate, datetime('now', 'local')), 'start of month', '-1 day') * 12 + 
                                strftime('%m', ifnull(psl.rateStartDate, datetime('now', 'local')), 'start of month', '-1 day') -
                            strftime('%Y', psc.fromServiceDate) * 12 - strftime('%m', psc.fromServiceDate) +
                                (strftime('%d', ifnull(psl.rateStartDate, datetime('now', 'local')), '+1 day') = '01' OR 
                                strftime('%d', ifnull(psl.rateStartDate, datetime('now', 'local'))) >= strftime('%d', psc.fromServiceDate))
                    ELSE strftime('%Y', ifnull(psl.rateStartDate, datetime('now', 'local')), 'start of month', '-1 day') * 12 + 
                                strftime('%m', ifnull(psl.rateStartDate, datetime('now', 'local')), 'start of month', '-1 day') -
                            strftime('%Y', psc.rateStartDate) * 12 - strftime('%m', psc.rateStartDate) +
                                (strftime('%d', ifnull(psl.rateStartDate, datetime('now', 'local')), '+1 day') = '01' OR 
                                strftime('%d', ifnull(psl.rateStartDate, datetime('now', 'local'))) >= strftime('%d', psc.rateStartDate))
                END)
            ELSE COUNT(psc.payerServiceId)
        END) AS fullMonths, 
        psc.servicePos, psc.serviceType, psc.serviceName, psc.serviceLocCode, psc.rateValue, 
        psc.fromMeterValue, psc.toMeterValue, SUM(psc.diffMeterValue) AS diffMeterValue, psc.measureUnit, 
        SUM(psc.serviceDebt) AS serviceDebt, 
        psc.isMeterUses 
    FROM ${PayerServiceDebtView.VIEW_NAME} psc LEFT JOIN ${PayerServiceDebtView.VIEW_NAME} psl ON psl.isMeterUses = 0  
    -- Payer services without meters and with fromServiceDate for correct: from... toPaymentDate and debt factor (full months)
        AND psl.fromServiceDate = psc.fromServiceDate
        AND psl.payerId = psc.payerId 
        AND psl.payerServiceId = psc.payerServiceId
        AND psl.serviceLocCode = psc.serviceLocCode
        AND strftime(${Constants.DB_FRACT_SEC_TIME}, psl.rateStartDate) = 
            (SELECT MIN(strftime(${Constants.DB_FRACT_SEC_TIME}, psd.rateStartDate)) 
            FROM ${PayerServiceDebtView.VIEW_NAME} psd
            WHERE psd.isMeterUses = psl.isMeterUses
                AND psd.fromServiceDate = psl.fromServiceDate
                AND psd.payerId = psl.payerId 
                AND psd.payerServiceId = psl.payerServiceId
                AND psd.serviceLocCode = psl.serviceLocCode
                AND strftime(${Constants.DB_FRACT_SEC_TIME}, psd.rateStartDate) >= strftime(${Constants.DB_FRACT_SEC_TIME}, psl.fromServiceDate)
                AND strftime(${Constants.DB_FRACT_SEC_TIME}, psd.rateStartDate) >= strftime(${Constants.DB_FRACT_SEC_TIME}, psl.rateStartDate))
    GROUP BY psc.payerId, psc.serviceId, psc.payerServiceId, 
        psc.fromServiceDate, psc.servicePos, psc.serviceType, psc.serviceName, psc.serviceLocCode, psc.rateValue, 
        psc.rateStartDate, psl.rateStartDate, 
        psc.fromMeterValue, psc.toMeterValue, psc.measureUnit, psc.isMeterUses) ps
ORDER BY payerId, servicePos, fromPaymentDate, fromMeterValue
"""
)
class PayerServiceSubtotalDebtView(
    val payerId: UUID,
    @field:TypeConverters(DateTypeConverter::class)
    val fromPaymentDate: OffsetDateTime,
    @field:TypeConverters(DateTypeConverter::class)
    val toPaymentDate: OffsetDateTime,
    val serviceId: UUID,
    val payerServiceId: UUID,
    val fullMonths: Int,
    val servicePos: Int,
    val serviceType: ServiceType,
    val serviceName: String,
    val serviceLocCode: String,
    val rateValue: BigDecimal,
    val fromMeterValue: BigDecimal?,
    val toMeterValue: BigDecimal?,
    val diffMeterValue: BigDecimal?,
    val measureUnit: String?,
    val serviceDebt: BigDecimal,
    val isMeterUses: Boolean
) {
    companion object {
        const val VIEW_NAME = "payer_service_subtotal_debts_view"
    }
}