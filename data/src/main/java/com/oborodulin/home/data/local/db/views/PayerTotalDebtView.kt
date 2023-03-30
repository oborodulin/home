package com.oborodulin.home.data.local.db.views

import androidx.room.DatabaseView
import androidx.room.TypeConverters
import com.oborodulin.home.data.local.db.converters.DateTypeConverter
import com.oborodulin.home.data.util.Constants
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*

@DatabaseView(
    viewName = PayerTotalDebtView.VIEW_NAME,
    value = """
SELECT ptb.payerId, 
        strftime(${Constants.DB_FRACT_SEC_TIME}, datetime(ptb.fromPaymentDate, 'localtime')) || 
            printf('%+.2d:%.2d', round((julianday(ptb.fromPaymentDate, 'localtime') - julianday(ptb.fromPaymentDate)) * 24), 
                abs(round((julianday(ptb.fromPaymentDate, 'localtime') - julianday(ptb.fromPaymentDate)) * 24 * 60) % 60)) AS fromPaymentDate, 
        strftime(${Constants.DB_FRACT_SEC_TIME}, datetime(ptb.toPaymentDate, 'localtime')) || 
            printf('%+.2d:%.2d', round((julianday(ptb.toPaymentDate, 'localtime') - julianday(ptb.toPaymentDate)) * 24), 
                abs(round((julianday(ptb.toPaymentDate, 'localtime') - julianday(ptb.toPaymentDate)) * 24 * 60) % 60)) AS toPaymentDate, 
        ptb.serviceLocCode, ptb.totalDebt
FROM (SELECT payerId, 
        MIN(strftime(${Constants.DB_FRACT_SEC_TIME}, fromPaymentDate)) AS fromPaymentDate, 
        MAX(strftime(${Constants.DB_FRACT_SEC_TIME}, toPaymentDate)) AS toPaymentDate, 
        serviceLocCode, SUM(serviceDebt) AS totalDebt
    FROM ${PayerServiceSubtotalDebtView.VIEW_NAME}
    GROUP BY payerId, serviceLocCode) ptb    
"""
)
class PayerTotalDebtView(
    val payerId: UUID,
    @field:TypeConverters(DateTypeConverter::class)
    val fromPaymentDate: OffsetDateTime? = null,
    @field:TypeConverters(DateTypeConverter::class)
    val toPaymentDate: OffsetDateTime? = null,
    val serviceLocCode: String?,
    val totalDebt: BigDecimal = BigDecimal.ZERO
) {
    companion object {
        const val VIEW_NAME = "payer_total_debts_view"
    }
}