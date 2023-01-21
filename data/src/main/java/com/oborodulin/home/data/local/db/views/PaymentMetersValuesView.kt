package com.oborodulin.home.data.local.db.views

import androidx.room.DatabaseView
import androidx.room.Embedded
import com.oborodulin.home.data.local.db.entities.MeterEntity
import com.oborodulin.home.data.util.Constants
import java.math.BigDecimal
import java.util.*

@DatabaseView(
    viewName = PaymentMetersValuesView.VIEW_NAME,
    value = "SELECT lmv.payerId, lmv.payerServiceId, lmv.metersId, pmv.meterValue AS startMeterValue, lmv.meterValue AS endMeterValue, " +
            "(CASE " +
            "WHEN instr((SELECT measureUnit FROM meters_tl WHERE metersId = lmv.metersId LIMIT 1), '/') = 0 THEN 0 ELSE 1 " +
            "END) AS derivedUnit, " +
            "(CASE WHEN instr((SELECT measureUnit FROM meters_tl WHERE metersId = lmv.metersId LIMIT 1), '/') = 0 " +
            "THEN (lmv.meterValue - pmv.meterValue) " +
            "ELSE lmv.meterValue END) AS diffMeterValue, " +
            "lmv.paymentYear, lmv.paymentMonth " +
            "FROM last_meters_values_view lmv JOIN last_meters_values_view pmv " +
            "ON pmv.metersId = lmv.metersId " +
            "AND pmv.paymentYear = (CASE WHEN (lmv.paymentMonth - 1) = 0 THEN lmv.paymentYear - 1 ELSE lmv.paymentYear END) " +
            "AND pmv.paymentMonth = (CASE WHEN (lmv.paymentMonth - 1) = 0 THEN 12 ELSE lmv.paymentMonth - 1 END)"
)
class PaymentMetersValuesView(
    val payerId: UUID,
    val payerServiceId: UUID,
    val metersId: UUID,
    val startMeterValue: BigDecimal,
    val endMeterValue: BigDecimal,
    val derivedUnit: Boolean,
    val diffMeterValue: BigDecimal,
    val paymentYear: Int,
    val paymentMonth: Int
) {
    companion object {
        const val VIEW_NAME = "payment_meters_values_view"
    }
}