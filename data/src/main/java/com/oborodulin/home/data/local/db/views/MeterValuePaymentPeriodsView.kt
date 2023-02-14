package com.oborodulin.home.data.local.db.views

import androidx.room.DatabaseView
import androidx.room.Embedded
import com.oborodulin.home.data.local.db.entities.MeterValueEntity
import com.oborodulin.home.data.util.Constants
import java.time.OffsetDateTime
import java.util.*

@DatabaseView(
    viewName = MeterValuePaymentPeriodsView.VIEW_NAME,
    value = """
SELECT mv.*, lv.payerId, lv.payerServiceId, lv.paymentDate,
    CAST(strftime('%m', lv.paymentDate) AS INTEGER) AS paymentMonth, 
    CAST(strftime('%Y', lv.paymentDate) AS INTEGER) AS paymentYear
FROM meter_values mv JOIN 
        (SELECT p.payerId, ps.payerServiceId, m.meterId, MAX(datetime(v.valueDate)) maxValueDate, 
            (CASE 
                WHEN datetime(v.valueDate) 
                    BETWEEN datetime(v.valueDate, 'start of month') 
                        AND datetime(v.valueDate, 'start of month', '+' || (IFNULL(p.paymentDay, ${Constants.DEF_PAYMENT_DAY}) - 1) || ' days') 
                    THEN datetime(v.valueDate, 'start of month') 
                WHEN datetime(v.valueDate) 
                    BETWEEN datetime(v.valueDate, 'start of month', '+' || IFNULL(p.paymentDay, ${Constants.DEF_PAYMENT_DAY}) || ' days') 
                        AND datetime(v.valueDate, '+1 months', 'start of month') 
                    THEN datetime(v.valueDate, '+1 months', 'start of month') 
            END) paymentDate
        FROM meter_values v JOIN meters m ON m.meterId = v.metersId 
            JOIN payers_services_meters AS psm ON psm.metersId = m.meterId 
            JOIN payers_services AS ps ON ps.payerServiceId = psm.payersServicesId 
            JOIN payers AS p ON p.payerId = ps.payersId 
        GROUP BY p.payerId, ps.payerServiceId, m.meterId,
            (CASE 
                WHEN datetime(v.valueDate) 
                    BETWEEN datetime(v.valueDate, 'start of month') 
                        AND datetime(v.valueDate, 'start of month', '+' || (IFNULL(p.paymentDay, ${Constants.DEF_PAYMENT_DAY}) - 1) || ' days') 
                    THEN datetime(v.valueDate, 'start of month') 
                WHEN datetime(v.valueDate) 
                    BETWEEN datetime(v.valueDate, 'start of month', '+' || IFNULL(p.paymentDay, ${Constants.DEF_PAYMENT_DAY}) || ' days') 
                        AND datetime(v.valueDate, '+1 months', 'start of month') 
                    THEN datetime(v.valueDate, '+1 months', 'start of month') 
            END)) lv 
    ON mv.metersId = lv.meterId AND datetime(mv.valueDate) = lv.maxValueDate        
    """
)
class MeterValuePaymentPeriodsView(
    @Embedded
    val data: MeterValueEntity,
    val payerId: UUID,
    val payerServiceId: UUID,
    val paymentDate: OffsetDateTime,
    val paymentMonth: Int,
    val paymentYear: Int
) {
    companion object {
        const val VIEW_NAME = "meter_value_payment_periods_view"
    }
}