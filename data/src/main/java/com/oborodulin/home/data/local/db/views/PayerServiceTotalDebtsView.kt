package com.oborodulin.home.data.local.db.views

import androidx.room.DatabaseView
import com.oborodulin.home.data.util.ServiceType
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*

@DatabaseView(
    viewName = PayerServiceTotalDebtsView.VIEW_NAME,
    value = """
SELECT ptb.payerId, MIN(ptb.paymentDate) AS fromPaymentDate, MAX(ptb.paymentDate) AS toPaymentDate, 
        COUNT(ptb.payerServiceId) AS MonthsCount, ptb.pos, ptb.type, ptb.name, ptb.rateValue, 
        ptb.fromMeterValue, ptb.toMeterValue, SUM(ptb.diffMeterValue) AS diffMeterValue, ptb.measureUnit,
        SUM(ptb.serviceDebt) AS serviceDebt, ptb.isMeterUses
FROM payer_service_debts_view ptb
GROUP BY ptb.payerId, ptb.pos, ptb.type, ptb.name, ptb.rateValue, 
        ptb.fromMeterValue, ptb.toMeterValue, ptb.measureUnit, ptb.isMeterUses
ORDER BY ptb.payerId, ptb.paymentDate, ptb.pos
"""
)
class PayerServiceTotalDebtsView(
    val payerId: UUID,
    val fromPaymentDate: OffsetDateTime,
    val toPaymentDate: OffsetDateTime,
    val MonthsCount: Int,
    val pos: Int,
    val type: ServiceType,
    val name: String,
    val rateValue: BigDecimal,
    val fromMeterValue: BigDecimal?,
    val toMeterValue: BigDecimal?,
    val diffMeterValue: BigDecimal?,
    val measureUnit: String?,
    val serviceDebt: BigDecimal,
    val isMeterUses: Boolean
) {
    companion object {
        const val VIEW_NAME = "payer_service_total_debts_view"
    }
}