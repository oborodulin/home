package com.oborodulin.home.data.local.db.views

import androidx.room.DatabaseView
import com.oborodulin.home.data.util.Constants
import com.oborodulin.home.data.util.Constants.DB_FALSE
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*


@DatabaseView(
    viewName = MeterValuePaymentView.VIEW_NAME,
    value = """
SELECT mv.payerId, mv.payerServiceId, mv.meterId, mv.meterValueId, 
        mv.startMeterValue, mv.endMeterValue, 
        (CASE WHEN mv.diffMonths > 1 
            THEN CASE WHEN mv.isDerivedUnit = $DB_FALSE 
                    THEN mv.diffMeterValue / mv.diffMonths
                    ELSE (mv.endMeterValue + mv.startMeterValue) / 2.0 
                END 
            ELSE mv.diffMeterValue
        END) AS diffMeterValue, 
        mv.isDerivedUnit, mv.derivedUnit, mv.measureUnit, 
        mv.fromPaymentDate, mv.toPaymentDate, 
        mv.diffMonths, mv.paymentMonth, mv.paymentYear, mv.meterLocCode, mv.servicePos
FROM (SELECT cmv.payerId, cmv.payerServiceId, cmv.meterId, nmv.meterValueId, 
        cmv.meterValue AS startMeterValue, nmv.meterValue AS endMeterValue,
        (CASE WHEN nmv.isDerivedUnit = $DB_FALSE 
            THEN CASE WHEN nmv.meterValue >= cmv.meterValue 
                    THEN nmv.meterValue - cmv.meterValue
                    ELSE (cmv.maxValue - cmv.meterValue) + nmv.meterValue
                END 
            ELSE nmv.meterValue
        END) AS diffMeterValue, 
        cmv.isDerivedUnit, cmv.derivedUnit, cmv.measureUnit, 
        cmv.paymentDate AS fromPaymentDate, nmv.paymentDate AS toPaymentDate, 
        (strftime('%Y', ifnull(nmv.paymentDate, datetime('now', 'localtime')), 'start of month', '-1 day') * 12 + 
            strftime('%m', ifnull(nmv.paymentDate, datetime('now', 'localtime')), 'start of month', '-1 day') -
        strftime('%Y', cmv.paymentDate) * 12 - strftime('%m', cmv.paymentDate) +
            (strftime('%d', ifnull(nmv.paymentDate, datetime('now', 'localtime')), '+1 day') = '01' OR 
            strftime('%d', ifnull(nmv.paymentDate, datetime('now', 'localtime'))) >= strftime('%d', cmv.paymentDate))) AS diffMonths,
        nmv.paymentMonth, nmv.paymentYear, cmv.meterLocCode, cmv.servicePos 
    FROM ${MeterValuePaymentPeriodView.VIEW_NAME} cmv LEFT JOIN ${MeterValuePaymentPeriodView.VIEW_NAME} nmv
        ON nmv.meterId = cmv.meterId
            AND nmv.payerServiceId = cmv.payerServiceId
            AND nmv.meterLocCode = cmv.meterLocCode
            AND strftime(${Constants.DB_FRACT_SEC_TIME}, nmv.paymentDate) = 
                (SELECT MIN(strftime(${Constants.DB_FRACT_SEC_TIME}, mvp.paymentDate))
                FROM ${MeterValuePaymentPeriodView.VIEW_NAME} mvp
                WHERE mvp.meterId = nmv.meterId
                    AND mvp.payerServiceId = nmv.payerServiceId
                    AND mvp.meterLocCode = nmv.meterLocCode
                    AND strftime(${Constants.DB_FRACT_SEC_TIME}, mvp.paymentDate) > strftime(${Constants.DB_FRACT_SEC_TIME}, cmv.paymentDate))) mv
"""
)
class MeterValuePaymentView(
    val payerId: UUID,
    val payerServiceId: UUID,
    val meterId: UUID,
    val meterValueId: UUID?,
    val startMeterValue: BigDecimal,
    val endMeterValue: BigDecimal?,
    val diffMeterValue: BigDecimal?,
    val isDerivedUnit: Boolean,
    val derivedUnit: String?,
    val measureUnit: String,
    val fromPaymentDate: OffsetDateTime,
    val toPaymentDate: OffsetDateTime?,
    val diffMonths: Int,
    val paymentMonth: Int?,
    val paymentYear: Int?,
    val meterLocCode: String,
    val servicePos: Int // for testing purpose
) {
    companion object {
        const val VIEW_NAME = "meter_value_payments_view"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MeterValuePaymentView
        meterValueId?.let { if (it != (other.meterValueId ?: "")) return false }
        if (meterId != other.meterId || startMeterValue != other.startMeterValue) return false

        return true
    }

    /***
     * https://stackoverflow.com/questions/54074167/create-an-unique-hashcode-based-on-many-values
     * https://stackoverflow.com/questions/16824721/generating-hashcode-from-multiple-fields
     */
    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + meterValueId.hashCode()
        result = prime * result + meterId.hashCode()
        result = prime * result + startMeterValue.hashCode()
        return result  //(meterId.hashCode() << 1) + startMeterValue.hashCode()
    }

    override fun toString(): String {
        val str = StringBuffer()
        str.append("Meter [meterId = ").append(meterId).append("] Value from ")
            .append(startMeterValue)
            .append(" to ")
            .append(endMeterValue ?: "...")
        endMeterValue?.let {
            str.append(" [diffMeterValue = ").append(diffMeterValue).append("]")
        }
        str.append(" for Payment from ")
            .append(DateTimeFormatter.ISO_LOCAL_DATE.format(fromPaymentDate))
            .append(" to ")
            .append(if (toPaymentDate != null) DateTimeFormatter.ISO_LOCAL_DATE.format(toPaymentDate) else "...")
            .append(" (").append(diffMonths).append(" months)")
            .append(" [payerId = ").append(payerId)
            .append("] meterValueId = ").append(meterValueId)
        return str.toString()
    }
}