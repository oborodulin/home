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
SELECT mv.*, lv.measureUnit, lv.payerId, lv.payerServiceId, lv.paymentDate,
    CAST(strftime('%m', lv.paymentDate) AS INTEGER) AS paymentMonth, 
    CAST(strftime('%Y', lv.paymentDate) AS INTEGER) AS paymentYear
FROM meter_values mv JOIN 
    (SELECT mvp.payerId, ps.payerServiceId, mvp.meterId, MAX(datetime(mvp.valueDate)) maxValueDate, mvp.paymentDate,
        IFNULL(sv.measureUnit, mvp.measureUnit) AS measureUnit
    FROM (SELECT m.meterId, p.payerId, m.type, v.valueDate, mtl.measureUnit,
            (CASE WHEN p.isAlignByPaymentDay = 0
                THEN datetime(v.valueDate, 'start of month')
                ELSE
                    CASE WHEN datetime(v.valueDate) 
                            BETWEEN datetime(v.valueDate, 'start of month') 
                                AND datetime(v.valueDate, 'start of month', '+' || (IFNULL(p.paymentDay, ${Constants.DEF_PAYMENT_DAY}) - 1) || ' days') 
                        THEN datetime(v.valueDate, 'start of month') 
                        WHEN datetime(v.valueDate) 
                            BETWEEN datetime(v.valueDate, 'start of month', '+' || IFNULL(p.paymentDay, ${Constants.DEF_PAYMENT_DAY}) || ' days') 
                                AND datetime(v.valueDate, '+1 months', 'start of month') 
                        THEN datetime(v.valueDate, '+1 months', 'start of month') 
                    END
            END) paymentDate
        FROM meter_values v JOIN meters m ON m.meterId = v.metersId
            JOIN meters_tl AS mtl ON mtl.metersId = m.meterId
            JOIN payers p ON p.payerId = m.payersId) mvp
        JOIN payers_services_meters AS psm ON psm.metersId = mvp.meterId 
        JOIN payers_services AS ps ON ps.payerServiceId = psm.payersServicesId
        JOIN services_view sv ON sv.serviceId = ps.servicesId AND sv.type = mvp.type
    GROUP BY mvp.payerId, ps.payerServiceId, mvp.meterId, mvp.paymentDate) lv 
        ON mv.metersId = lv.meterId AND datetime(mv.valueDate) = lv.maxValueDate
"""
)
class MeterValuePaymentPeriodsView(
    @Embedded
    val data: MeterValueEntity,
    val measureUnit: String,
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