package com.oborodulin.home.data.local.db.views

import androidx.room.DatabaseView
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*

@DatabaseView(
    viewName = PayerTotalDebtsView.VIEW_NAME,
    value = """
SELECT pst.payerId, MIN(pst.fromPaymentDate) AS fromPaymentDate, MAX(pst.toPaymentDate) AS toPaymentDate, 
        SUM(pst.serviceDebt) AS payerDebt
FROM payer_service_total_debts_view pst
GROUP BY pst.payerId
"""
)
class PayerTotalDebtsView(
    val payerId: UUID,
    val fromPaymentDate: OffsetDateTime?,
    val toPaymentDate: OffsetDateTime?,
    val payerDebt: BigDecimal?
) {
    companion object {
        const val VIEW_NAME = "payer_total_debts_view"
    }
}