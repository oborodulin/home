package com.oborodulin.home.data.local.db.views

import androidx.room.DatabaseView
import androidx.room.TypeConverters
import com.oborodulin.home.data.local.db.converters.DateTypeConverter
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*

@DatabaseView(
    viewName = PayerTotalDebtView.VIEW_NAME,
    value = """
SELECT payerId, MIN(fromPaymentDate) AS fromPaymentDate, MAX(toPaymentDate) AS toPaymentDate, 
        serviceLocaleCode, SUM(serviceDebt) AS totalDebt
FROM ${PayerServiceSubtotalDebtView.VIEW_NAME}
GROUP BY payerId, serviceLocaleCode
"""
)
class PayerTotalDebtView(
    val payerId: UUID,
    @field:TypeConverters(DateTypeConverter::class)
    val fromPaymentDate: OffsetDateTime? = null,
    @field:TypeConverters(DateTypeConverter::class)
    val toPaymentDate: OffsetDateTime? = null,
    val serviceLocaleCode: String?,
    val totalDebt: BigDecimal = BigDecimal.ZERO
) {
    companion object {
        const val VIEW_NAME = "payer_total_debts_view"
    }
}