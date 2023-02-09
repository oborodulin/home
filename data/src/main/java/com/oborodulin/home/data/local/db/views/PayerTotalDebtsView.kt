package com.oborodulin.home.data.local.db.views

import androidx.room.DatabaseView
import com.oborodulin.home.data.util.ServiceType
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*

@DatabaseView(
    viewName = PayerTotalDebtsView.VIEW_NAME,
    value = "SELECT mvl.meterValueId, mv.payersId AS payerId, mv.servicesId AS serviceId, " +
            "sv.type, sv.name, sv.pos, mv.meterId, IFNULL(mv.measureUnit, sv.measureUnit) AS measureUnit, " +
            "mvl.valueDate AS prevLastDate, mvl.meterValue AS prevValue, p.isFavorite, " +
            "mv.localeCode AS meterlocaleCode, sv.localeCode AS servicelocaleCode " +
            "FROM meters_view AS mv JOIN services_view AS sv ON sv.serviceId = mv.servicesId " +
            "JOIN meter_values AS mvl ON mvl.metersId = mv.meterId " +
            "JOIN payers AS p ON p.payerId = mv.payersId " //+
            /*"JOIN " +
            "(SELECT v.metersId, MAX(v.valueDate) maxValueDate " +
            "FROM meter_values v JOIN meters m ON m.meterId = v.metersId " +
            "JOIN payers_services AS ps ON ps.payerServiceId = m.payersServicesId " +
            "JOIN payers AS p ON p.payerId = ps.payersId " +
            "WHERE v.valueDate <= CASE WHEN strftime('%s', 'now') > strftime('%s', 'now', 'start of month', 'start of month', '+' || IFNULL(p.paymentDay, 20) || ' days') " +
            "THEN strftime('%s', 'now', 'start of month', '+' || IFNULL(p.paymentDay, 20) || ' days') " +
            "ELSE strftime('%s', 'now', '-1 months', 'start of month', '+' || IFNULL(p.paymentDay, 20) || ' days') END * 1000 " +
            "GROUP BY v.metersId) mp " +
            "ON mp.metersId = mvl.metersId AND mp.maxValueDate = mvl.valueDate "*/
)
class PayerTotalDebtsView(
    var meterValueId: UUID,
    var payerId: UUID,
    var serviceId: UUID,
    var type: ServiceType,
    val name: String,
    var pos: Int,
    var meterId: UUID,
    var measureUnit: String,
    val prevLastDate: OffsetDateTime,
    val prevValue: BigDecimal?,
    var isFavorite: Boolean,
    val meterlocaleCode: String,
    val servicelocaleCode: String
) {
    companion object {
        const val VIEW_NAME = "payer_total_debts_view"
    }
}