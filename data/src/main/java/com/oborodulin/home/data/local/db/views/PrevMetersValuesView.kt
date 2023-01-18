package com.oborodulin.home.data.local.db.views

import androidx.room.DatabaseView
import com.oborodulin.home.data.util.ServiceType
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*

@DatabaseView(
    viewName = PrevMetersValuesView.VIEW_NAME,
    value = "SELECT mvl.meterValueId, mv.payersId AS payerId, mv.servicesId AS serviceId, " +
            "sv.type, sv.name, sv.pos, mv.meterId, IFNULL(mv.measureUnit, sv.measureUnit) AS measureUnit, " +
            "IFNULL(mvl.valueDate, datetime('now')) AS prevLastDate, mvl.meterValue AS prevValue, p.isFavorite, " +
            "mv.localeCode AS meterlocaleCode, sv.localeCode AS servicelocaleCode, " +
            "substr('#0.' || '0000000000', 1, 3+(length(cast(mv.maxValue as text)) -  " +
            "CASE WHEN instr(cast(mv.maxValue as text), '.') = 0 THEN length(cast(mv.maxValue as text)) + 1 ELSE instr(cast(mv.maxValue as text), '.') END)) AS valueFormat  " +
            "FROM meters_view AS mv JOIN services_view AS sv ON sv.serviceId = mv.servicesId  " +
            "JOIN meter_values AS mvl ON mvl.metersId = mv.meterId  " +
            "JOIN payers AS p ON p.payerId = mv.payersId  " +
            "JOIN  " +
            "(SELECT v.metersId, MAX(datetime(v.valueDate)) maxValueDate  " +
            "FROM meter_values v JOIN meters m ON m.meterId = v.metersId  " +
            "JOIN payers_services AS ps ON ps.payerServiceId = m.payersServicesId  " +
            "JOIN payers AS p ON p.payerId = ps.payersId  " +
            "WHERE datetime(v.valueDate) <= CASE WHEN datetime('now') > datetime('now', 'start of month', '+' || (IFNULL(p.paymentDay, 20) - 1) || ' days')  " +
            "THEN datetime('now', 'start of month', '+' || (IFNULL(p.paymentDay, 20) - 1) || ' days')  " +
            "ELSE datetime('now', '-1 months', 'start of month', '+' || (IFNULL(p.paymentDay, 20) - 1) || ' days') END  " +
            "GROUP BY v.metersId) mp  " +
            "ON mp.metersId = mvl.metersId AND mp.maxValueDate = datetime(mvl.valueDate)"
)
class PrevMetersValuesView(
    var meterValueId: UUID,
    var payerId: UUID,
    var serviceId: UUID,
    var type: ServiceType,
    var name: String,
    var pos: Int,
    var meterId: UUID,
    var measureUnit: String,
    var prevLastDate: OffsetDateTime,
    var prevValue: BigDecimal?,
    var isFavorite: Boolean,
    var meterlocaleCode: String,
    var servicelocaleCode: String,
    var valueFormat: String
) {
    companion object {
        const val VIEW_NAME = "prev_meters_values_view"
    }
}