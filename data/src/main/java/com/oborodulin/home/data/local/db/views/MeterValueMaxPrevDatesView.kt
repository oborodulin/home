package com.oborodulin.home.data.local.db.views

import androidx.room.DatabaseView
import com.oborodulin.home.data.util.Constants.DEF_PAYMENT_DAY
import java.time.OffsetDateTime
import java.util.*

@DatabaseView(
    viewName = MeterValueMaxPrevDatesView.VIEW_NAME,
    value = """
SELECT v.metersId, MAX(datetime(v.valueDate)) maxValueDate 
    FROM meter_values v JOIN meters m ON m.meterId = v.metersId
        JOIN payers_services AS ps ON ps.payerServiceId = m.payersServicesId
        JOIN payers AS p ON p.payerId = ps.payersId
    WHERE datetime(v.valueDate) <= CASE WHEN datetime('now') > datetime('now', 'start of month', '+' || (IFNULL(p.paymentDay, ${DEF_PAYMENT_DAY}) - 1) || ' days')
            THEN datetime('now', 'start of month', '+' || (IFNULL(p.paymentDay, ${DEF_PAYMENT_DAY}) - 1) || ' days')
            ELSE datetime('now', '-1 months', 'start of month', '+' || (IFNULL(p.paymentDay, ${DEF_PAYMENT_DAY}) - 1) || ' days') END
GROUP BY v.metersId
"""
)
class MeterValueMaxPrevDatesView(
    var metersId: UUID,
    var maxValueDate: OffsetDateTime
) {
    companion object {
        const val VIEW_NAME = "meter_value_max_prev_dates_view"
    }
}