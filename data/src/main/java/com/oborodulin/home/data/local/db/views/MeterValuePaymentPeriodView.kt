package com.oborodulin.home.data.local.db.views

import androidx.room.DatabaseView
import androidx.room.Embedded
import com.oborodulin.home.data.local.db.entities.MeterValueEntity
import com.oborodulin.home.data.util.Constants
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*

@DatabaseView(
    viewName = MeterValuePaymentPeriodView.VIEW_NAME,
    value = """
SELECT mv.*, lv.measureUnit, lv.isDerivedUnit, lv.derivedUnit, lv.localeCode, lv.maxValue,
    lv.payerId, lv.payerServiceId, lv.paymentDate,
    CAST(strftime('%m', lv.paymentDate) AS INTEGER) AS paymentMonth, 
    CAST(strftime('%Y', lv.paymentDate) AS INTEGER) AS paymentYear
FROM meter_values mv JOIN 
    (SELECT mvp.payerId, ps.payerServiceId, mvp.meterId, MAX(strftime(${Constants.DB_FRACT_SEC_TIME}, mvp.valueDate)) maxValueDate, 
        mvp.paymentDate, IFNULL(sv.measureUnit, mvp.measureUnit) AS measureUnit, 
        mvp.isDerivedUnit, mvp.derivedUnit, mvp.localeCode, mvp.maxValue
    FROM (SELECT mv.meterId, p.payerId, mv.type, v.valueDate, mv.measureUnit, mv.isDerivedUnit, 
            mv.derivedUnit, mv.localeCode, mv.maxValue,
            (CASE WHEN p.isAlignByPaymentDay = 0
                THEN strftime(${Constants.DB_FRACT_SEC_TIME}, v.valueDate, 'start of month')
                ELSE
                    CASE WHEN datetime(v.valueDate) 
                            BETWEEN datetime(v.valueDate, 'start of month') 
                                AND datetime(v.valueDate, 'start of month', '+' || (IFNULL(p.paymentDay, ${Constants.DEF_PAYMENT_DAY}) - 1) || ' days') 
                        THEN strftime(${Constants.DB_FRACT_SEC_TIME}, v.valueDate, 'start of month')
                        WHEN datetime(v.valueDate) 
                            BETWEEN datetime(v.valueDate, 'start of month', '+' || IFNULL(p.paymentDay, ${Constants.DEF_PAYMENT_DAY}) || ' days') 
                                AND datetime(v.valueDate, '+1 months', 'start of month') 
                        THEN strftime(${Constants.DB_FRACT_SEC_TIME}, v.valueDate, '+1 months', 'start of month') 
                    END
            END) paymentDate
        FROM meter_values v JOIN ${MeterView.VIEW_NAME} mv ON mv.meterId = v.metersId
            JOIN payers p ON p.payerId = mv.payersId) mvp
        JOIN payers_services_meters AS psm ON psm.metersId = mvp.meterId 
        JOIN payers_services AS ps ON ps.payerServiceId = psm.payersServicesId
        JOIN services_view sv ON sv.serviceId = ps.servicesId AND sv.localeCode = mvp.localeCode
    GROUP BY mvp.payerId, ps.payerServiceId, mvp.meterId, mvp.paymentDate, sv.measureUnit, mvp.measureUnit,
            mvp.isDerivedUnit, mvp.derivedUnit, mvp.localeCode, mvp.maxValue) lv 
        ON mv.metersId = lv.meterId 
        AND strftime(${Constants.DB_FRACT_SEC_TIME}, mv.valueDate) = lv.maxValueDate
"""
)
class MeterValuePaymentPeriodView(
    @Embedded
    val data: MeterValueEntity,
    val measureUnit: String,
    val isDerivedUnit: Boolean,
    val derivedUnit: String,
    val localeCode: String,
    val maxValue: BigDecimal,
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