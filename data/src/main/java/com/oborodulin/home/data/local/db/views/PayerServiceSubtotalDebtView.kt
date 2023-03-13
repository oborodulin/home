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
SELECT pss.payerId, 
        STRFTIME(${Constants.DB_FRACT_SEC_TIME}, DATETIME(pss.fromPaymentDate, 'localtime')) || 
            PRINTF('%+.2d:%.2d', ROUND((JULIANDAY(pss.fromPaymentDate, 'localtime') - JULIANDAY(pss.fromPaymentDate)) * 24), 
                ABS(ROUND((JULIANDAY(pss.fromPaymentDate, 'localtime') - JULIANDAY(pss.fromPaymentDate)) * 24 * 60) % 60)) AS fromPaymentDate, 
        STRFTIME(${Constants.DB_FRACT_SEC_TIME}, DATETIME(pss.toPaymentDate, 'localtime')) || 
            PRINTF('%+.2d:%.2d', ROUND((JULIANDAY(pss.toPaymentDate, 'localtime') - JULIANDAY(pss.toPaymentDate)) * 24), 
                ABS(ROUND((JULIANDAY(pss.toPaymentDate, 'localtime') - JULIANDAY(pss.toPaymentDate)) * 24 * 60) % 60)) AS toPaymentDate, 
        pss.serviceId, pss.payerServiceId, pss.MonthsCount, pss.servicePos, pss.serviceType, pss.serviceName, 
        pss.serviceLocCode, pss.rateValue, pss.fromMeterValue, pss.toMeterValue, 
        pss.diffMeterValue, measureUnit, pss.serviceDebt, pss.isMeterUses
FROM (SELECT payerId, 
            MIN(strftime(${Constants.DB_FRACT_SEC_TIME}, paymentDate)) AS fromPaymentDate, 
            MAX(strftime(${Constants.DB_FRACT_SEC_TIME}, paymentDate)) AS toPaymentDate, 
            serviceId, payerServiceId, COUNT(payerServiceId) AS monthsCount, servicePos, serviceType, serviceName, 
            serviceLocCode, rateValue, fromMeterValue, toMeterValue, 
            SUM(diffMeterValue) AS diffMeterValue, measureUnit, SUM(serviceDebt) AS serviceDebt, 
            isMeterUses
    FROM ${PayerServiceDebtView.VIEW_NAME}
    GROUP BY payerId, serviceId, payerServiceId, servicePos, serviceType, serviceName, serviceLocCode, rateValue, 
            fromMeterValue, toMeterValue, measureUnit, isMeterUses) pss
ORDER BY payerId, fromPaymentDate, servicePos    
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
    val monthsCount: Int,
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