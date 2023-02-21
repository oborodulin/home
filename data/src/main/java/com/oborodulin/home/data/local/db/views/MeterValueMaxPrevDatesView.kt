package com.oborodulin.home.data.local.db.views

import androidx.room.DatabaseView
import com.oborodulin.home.data.util.Constants
import java.time.OffsetDateTime
import java.util.*

@DatabaseView(
    viewName = MeterValueMaxPrevDatesView.VIEW_NAME,
    value = """
SELECT mv.meterId, MAX(mv.valueDate) AS maxValueDate 
FROM (SELECT m.meterId, m.payersId, IFNULL(datetime(v.valueDate), datetime('now', 'start of month', '-1 days')) valueDate 
        FROM meters m LEFT JOIN meter_values v ON v.metersId = m.meterId) mv 
    JOIN payers p ON p.payerId = mv.payersId
 WHERE datetime(mv.valueDate) <= 
    CASE WHEN p.isAlignByPaymentDay = 0 
        THEN datetime('now', 'start of month', '-1 days')
        ELSE CASE WHEN datetime('now') > datetime('now', 'start of month', '+' || (IFNULL(p.paymentDay, ${Constants.DEF_PAYMENT_DAY}) - 1) || ' days')
                THEN datetime('now', 'start of month', '+' || (IFNULL(p.paymentDay, ${Constants.DEF_PAYMENT_DAY}) - 1) || ' days')
                ELSE datetime('now', '-1 months', 'start of month', '+' || (IFNULL(p.paymentDay, ${Constants.DEF_PAYMENT_DAY}) - 1) || ' days')
            END
    END
GROUP BY mv.meterId
"""
)
class MeterValueMaxPrevDatesView(
    val meterId: UUID,
    val maxValueDate: OffsetDateTime
) {
    companion object {
        const val VIEW_NAME = "meter_value_max_prev_dates_view"
    }
}