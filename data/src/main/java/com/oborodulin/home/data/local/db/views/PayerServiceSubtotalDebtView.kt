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
        ps.servicePos, ps.serviceType, ps.serviceName, ps.serviceLocCode,
        ps.fromMeterValue, ps.toMeterValue, ps.diffMeterValue, ps.measureUnit, 
        ps.serviceDebt
FROM (SELECT psc.payerId, 
        MIN(strftime(${Constants.DB_FRACT_SEC_TIME}, psc.fromPaymentDate)) AS fromPaymentDate, 
        MAX(strftime(${Constants.DB_FRACT_SEC_TIME}, psc.toPaymentDate)) AS toPaymentDate, 
        psc.serviceId, psc.payerServiceId, SUM(psc.fullMonths) AS fullMonths, 
        psc.servicePos, psc.serviceType, psc.serviceName, psc.serviceLocCode,
        MIN(psc.fromMeterValue) AS fromMeterValue, MAX(psc.toMeterValue) AS toMeterValue, 
        SUM(psc.diffMeterValue) AS diffMeterValue, psc.measureUnit, 
        SUM(psc.serviceDebt) AS serviceDebt
    FROM ${PayerServiceDebtView.VIEW_NAME} psc
    GROUP BY psc.payerId, psc.serviceId, psc.payerServiceId, 
        psc.servicePos, psc.serviceType, psc.serviceName, psc.serviceLocCode, 
        psc.measureUnit) ps
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
    val fromMeterValue: BigDecimal?,
    val toMeterValue: BigDecimal?,
    val diffMeterValue: BigDecimal?,
    val measureUnit: String?,
    val serviceDebt: BigDecimal
) {
    companion object {
        const val VIEW_NAME = "payer_service_subtotal_debts_view"
    }
}