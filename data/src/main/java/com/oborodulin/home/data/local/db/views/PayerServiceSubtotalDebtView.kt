package com.oborodulin.home.data.local.db.views

import androidx.room.DatabaseView
import androidx.room.TypeConverters
import com.oborodulin.home.common.util.Constants.APP_DAY_DATE_TIME
import com.oborodulin.home.data.local.db.converters.DateTypeConverter
import com.oborodulin.home.data.util.Constants
import com.oborodulin.home.data.util.ServiceType
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@DatabaseView(
    viewName = PayerServiceSubtotalDebtView.VIEW_NAME,
    value = """
SELECT ps.payerId, 
        strftime(${Constants.DB_FRACT_SEC_TIME}, datetime(ps.fromPaymentDate, 'localtime')) || 
            printf('%+.2d:%.2d', round((julianday(ps.fromPaymentDate, 'localtime') - julianday(ps.fromPaymentDate)) * 24), 
                abs(round((julianday(ps.fromPaymentDate, 'localtime') - julianday(ps.fromPaymentDate)) * 24 * 60) % 60)) AS fromPaymentDate, 
        strftime(${Constants.DB_FRACT_SEC_TIME}, datetime(ps.toPaymentDate, 'localtime')) || 
            printf('%+.2d:%.2d', round((julianday(ps.toPaymentDate, 'localtime') - julianday(ps.toPaymentDate)) * 24), 
                abs(round((julianday(ps.toPaymentDate, 'localtime') - julianday(ps.toPaymentDate)) * 24 * 60) % 60)) AS toPaymentDate, 
        ps.serviceId, ps.payerServiceId, ps.fullMonths,
        ps.servicePos, ps.serviceType, ps.serviceName, ps.serviceLocCode,
        ps.startMeterValue, ps.endMeterValue, ps.diffMeterValue, ps.measureUnit, 
        ps.serviceDebt
FROM (SELECT psc.payerId, 
        MIN(strftime(${Constants.DB_FRACT_SEC_TIME}, psc.fromPaymentDate)) AS fromPaymentDate, 
        MAX(strftime(${Constants.DB_FRACT_SEC_TIME}, psc.toPaymentDate)) AS toPaymentDate, 
        psc.serviceId, psc.payerServiceId, 
        SUM(psc.fullMonths) AS fullMonths, 
        psc.servicePos, psc.serviceType, psc.serviceName, psc.serviceLocCode,
        MIN(psc.startMeterValue) AS startMeterValue, MAX(psc.endMeterValue) AS endMeterValue, 
        SUM(psc.diffMeterValue) AS diffMeterValue, 
        psc.measureUnit, 
        SUM(psc.serviceDebt) AS serviceDebt
    FROM ${PayerServiceDebtView.VIEW_NAME} psc
    GROUP BY psc.payerId, psc.serviceId, psc.payerServiceId, 
        psc.servicePos, psc.serviceType, psc.serviceName, psc.serviceLocCode, 
        psc.measureUnit) ps
ORDER BY payerId, servicePos, fromPaymentDate, startMeterValue
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
    val startMeterValue: BigDecimal?,
    val endMeterValue: BigDecimal?,
    val diffMeterValue: BigDecimal?,
    val measureUnit: String?,
    val serviceDebt: BigDecimal
) {
    companion object {
        const val VIEW_NAME = "payer_service_subtotal_debts_view"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PayerServiceSubtotalDebtView
        if (payerServiceId != other.payerServiceId) return false

        return true
    }

    override fun hashCode(): Int {
        return payerServiceId.hashCode()
    }

    override fun toString(): String {
        val str = StringBuffer()
        str.append("Payer Service '").append(serviceName).append("' Subtotal Debt from ")
            .append(DateTimeFormatter.ISO_LOCAL_DATE.format(fromPaymentDate))
            .append(" to ")
            .append(DateTimeFormatter.ISO_LOCAL_DATE.format(toPaymentDate))
            .append(" (").append(fullMonths).append(" months)")
            .append(": ").append(serviceDebt)
        startMeterValue?.let {
            str.append(" for ").append(it).append(" - ").append(endMeterValue ?: "...")
                .append(" [diffMeterValue = ").append(diffMeterValue).append("]")
        }
        str.append(" payerId = ").append(payerId).append("; payerServiceId = ")
            .append(payerServiceId)
        return str.toString()
    }
}