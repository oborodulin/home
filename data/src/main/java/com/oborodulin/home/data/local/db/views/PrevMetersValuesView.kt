package com.oborodulin.home.data.local.db.views

import androidx.room.DatabaseView
import com.oborodulin.home.common.util.Constants.CONV_COEFF_BIGDECIMAL
import com.oborodulin.home.data.util.Constants
import com.oborodulin.home.data.util.Constants.DEF_PAYMENT_DAY
import com.oborodulin.home.data.util.ServiceType
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*

@DatabaseView(
    viewName = PrevMetersValuesView.VIEW_NAME,
    value = "SELECT mvl.meterValueId, mv.payersId AS payerId, mv.servicesId AS serviceId, " +
            "sv.type, sv.name, sv.pos, mv.meterId, IFNULL(mv.measureUnit, sv.measureUnit) AS measureUnit, " +
            "IFNULL(mvl.valueDate, datetime('now')) AS prevLastDate, mvl.meterValue AS prevValue, p.isFavorite, " +
            "(SELECT vl.meterValue FROM meter_values vl " +
            "WHERE vl.metersId = mvl.metersId " +
            "AND datetime(vl.valueDate) = (SELECT MAX(datetime(v.valueDate)) FROM meter_values v " +
            "WHERE v.metersId = mvl.metersId " +
            "AND datetime(v.valueDate) > mp.maxValueDate) " +
            ") AS currentValue, " +
            "mv.localeCode AS meterlocaleCode, sv.localeCode AS servicelocaleCode, " +
            "substr('#0.' || '0000000000', 1, 3 + (length(cast(mv.maxValue / ${CONV_COEFF_BIGDECIMAL}.0 as text)) -  " +
            "CASE WHEN instr(cast(mv.maxValue / ${CONV_COEFF_BIGDECIMAL}.0 as text), '.') = length(cast(mv.maxValue / ${CONV_COEFF_BIGDECIMAL}.0 as text)) - 1 " +
            "THEN length(cast(mv.maxValue / ${CONV_COEFF_BIGDECIMAL}.0 as text)) + 1 ELSE instr(cast(mv.maxValue / ${CONV_COEFF_BIGDECIMAL}.0 as text), '.') END)) AS valueFormat  " +
            "FROM meters_view AS mv JOIN services_view AS sv ON sv.serviceId = mv.servicesId  " +
            "JOIN meter_values AS mvl ON mvl.metersId = mv.meterId  " +
            "JOIN payers AS p ON p.payerId = mv.payersId  " +
            "JOIN (" + Constants.SQL_PREV_METERS_VALUES_SUBQUERY +
            ") mp ON mp.metersId = mvl.metersId AND mp.maxValueDate = datetime(mvl.valueDate)"
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
    val currentValue: BigDecimal? = null,
    var meterlocaleCode: String,
    var servicelocaleCode: String,
    var valueFormat: String
) {
    companion object {
        const val VIEW_NAME = "prev_meters_values_view"
    }
}