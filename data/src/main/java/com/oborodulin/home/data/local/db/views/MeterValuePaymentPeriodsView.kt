package com.oborodulin.home.data.local.db.views

import androidx.room.DatabaseView
import androidx.room.Embedded
import com.oborodulin.home.data.local.db.entities.MeterValueEntity
import com.oborodulin.home.data.util.Constants
import java.util.*

@DatabaseView(
    viewName = MeterValuePaymentPeriodsView.VIEW_NAME,
    value = "SELECT mv.*, lv.payerId, lv.payerServiceId, lv.paymentYear, lv.paymentMonth FROM meter_values mv " +
            "JOIN " +
            "(SELECT p.payerId, ps.payerServiceId, m.meterId, MAX(datetime(v.valueDate)) maxValueDate, " +
            "CAST(strftime('%Y', v.valueDate) AS INTEGER) paymentYear, " +
            "CASE " +
            "WHEN datetime(v.valueDate) BETWEEN datetime(v.valueDate, 'start of month') " +
            "AND datetime(v.valueDate, 'start of month', '+' || (IFNULL(p.paymentDay, ${Constants.DEF_PAYMENT_DAY}) - 1) || ' days') " +
            "THEN CAST(strftime('%m', v.valueDate) AS INTEGER) " +
            "WHEN datetime(v.valueDate) BETWEEN datetime(v.valueDate, 'start of month', '+' || IFNULL(p.paymentDay, ${Constants.DEF_PAYMENT_DAY}) || ' days') " +
            "AND datetime(v.valueDate, '+1 months', 'start of month') " +
            "THEN CAST(strftime('%m', v.valueDate) AS INTEGER) + 1 " +
            "END paymentMonth " +
            "FROM meter_values v JOIN meters m ON m.meterId = v.metersId " +
            "JOIN payers_services AS ps ON ps.payerServiceId = m.payersServicesId " +
            "JOIN payers AS p ON p.payerId = ps.payersId " +
            "GROUP BY p.payerId, ps.payerServiceId, m.meterId, CAST(strftime('%Y', v.valueDate) AS INTEGER), " +
            "CASE " +
            "WHEN datetime(v.valueDate) BETWEEN datetime(v.valueDate, 'start of month') " +
            "AND datetime(v.valueDate, 'start of month', '+' || (IFNULL(p.paymentDay, ${Constants.DEF_PAYMENT_DAY}) - 1) || ' days') " +
            "THEN CAST(strftime('%m', v.valueDate) AS INTEGER) " +
            "WHEN datetime(v.valueDate) BETWEEN datetime(v.valueDate, 'start of month', '+' || IFNULL(p.paymentDay, ${Constants.DEF_PAYMENT_DAY}) || ' days') " +
            "AND datetime(v.valueDate, '+1 months', 'start of month') " +
            "THEN CAST(strftime('%m', v.valueDate) AS INTEGER) + 1 " +
            "END) lv " +
            "ON mv.metersId = lv.meterId AND datetime(mv.valueDate) = lv.maxValueDate"
)
class MeterValuePaymentPeriodsView(
    @Embedded
    var data: MeterValueEntity,
    var payerId: UUID,
    var paymentYear: Int,
    var paymentMonth: Int
) {
    companion object {
        const val VIEW_NAME = "meter_value_payment_periods_view"
    }
}