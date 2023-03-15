package com.oborodulin.home.data.local.db.views

import androidx.room.DatabaseView
import com.oborodulin.home.data.local.db.entities.MeterEntity
import com.oborodulin.home.data.local.db.entities.MeterValueEntity
import com.oborodulin.home.data.local.db.entities.PayerEntity
import com.oborodulin.home.data.util.Constants
import java.time.OffsetDateTime
import java.util.*

@DatabaseView(
    viewName = MeterValueMaxPrevDateView.VIEW_NAME,
    value = """
SELECT mv.meterId, MAX(mv.valueDate) AS maxValueDate 
FROM (SELECT m.meterId, m.payersId, 
            IFNULL(strftime(${Constants.DB_FRACT_SEC_TIME}, v.valueDate), 
                    IFNULL(strftime(${Constants.DB_FRACT_SEC_TIME}, m.passportDate), 
                            strftime(${Constants.DB_FRACT_SEC_TIME}, 'now', 'localtime', 'start of month', '-1 days'))) AS valueDate 
        FROM ${MeterEntity.TABLE_NAME} m LEFT JOIN ${MeterValueEntity.TABLE_NAME} v ON v.metersId = m.meterId) mv 
    JOIN ${PayerEntity.TABLE_NAME} p ON p.payerId = mv.payersId
 WHERE mv.valueDate <= 
    CASE WHEN p.isAlignByPaymentDay = 0 
        THEN strftime(${Constants.DB_FRACT_SEC_TIME}, 'now', 'localtime', 'start of month', '-1 days')
        ELSE CASE WHEN datetime('now', 'localtime') > datetime('now', 'localtime', 'start of month', '+' || (IFNULL(p.paymentDay, ${Constants.DEF_PAYMENT_DAY}) - 1) || ' days')
                THEN strftime(${Constants.DB_FRACT_SEC_TIME}, 'now', 'localtime', 'start of month', '+' || (IFNULL(p.paymentDay, ${Constants.DEF_PAYMENT_DAY}) - 1) || ' days')
                ELSE strftime(${Constants.DB_FRACT_SEC_TIME}, 'now', 'localtime', '-1 months', 'start of month', '+' || (IFNULL(p.paymentDay, ${Constants.DEF_PAYMENT_DAY}) - 1) || ' days')
            END
    END
GROUP BY mv.meterId
"""
)
class MeterValueMaxPrevDateView(
    val meterId: UUID,
    val maxValueDate: OffsetDateTime
) {
    companion object {
        const val VIEW_NAME = "meter_value_max_prev_dates_view"
    }
}