package com.oborodulin.home.data.local.db.views

import androidx.room.DatabaseView
import androidx.room.TypeConverters
import com.oborodulin.home.data.local.db.converters.DateTypeConverter
import com.oborodulin.home.data.util.ServiceType
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*

@DatabaseView(
    viewName = PayerServiceSubtotalDebtView.VIEW_NAME,
    value = """
SELECT payerId, MIN(paymentDate) AS fromPaymentDate, MAX(paymentDate) AS toPaymentDate, 
        serviceId, payerServiceId, COUNT(payerServiceId) AS MonthsCount, servicePos, serviceType, serviceName, 
        serviceLocCode, rateValue, fromMeterValue, toMeterValue, 
        SUM(diffMeterValue) AS diffMeterValue, measureUnit, SUM(serviceDebt) AS serviceDebt, 
        isMeterUses
FROM ${PayerServiceDebtView.VIEW_NAME}
GROUP BY payerId, serviceId, payerServiceId, servicePos, serviceType, serviceName, serviceLocCode, rateValue, 
        fromMeterValue, toMeterValue, measureUnit, isMeterUses
ORDER BY payerId, paymentDate, servicePos
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
    val MonthsCount: Int,
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