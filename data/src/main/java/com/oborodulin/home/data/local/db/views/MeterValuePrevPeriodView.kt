package com.oborodulin.home.data.local.db.views

import androidx.room.DatabaseView
import androidx.room.TypeConverters
import com.oborodulin.home.common.util.Constants
import com.oborodulin.home.data.local.db.converters.DateTypeConverter
import com.oborodulin.home.data.local.db.entities.MeterValueEntity
import com.oborodulin.home.data.local.db.entities.PayerEntity
import com.oborodulin.home.data.local.db.entities.PayerServiceCrossRefEntity
import com.oborodulin.home.data.util.Constants.DB_FRACT_SEC_TIME
import com.oborodulin.home.data.util.Constants.DB_TRUE
import com.oborodulin.home.data.util.MeterType
import com.oborodulin.home.data.util.ServiceType
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@DatabaseView(
    viewName = MeterValuePrevPeriodView.VIEW_NAME,
    value = """
SELECT mvf.meterValueId, p.payerId, sv.serviceId, sv.serviceType, sv.serviceName, sv.servicePos, 
    mvf.meterId, mvf.meterType, ifnull(mvf.meterMeasureUnit, sv.serviceMeasureUnit) AS measureUnit,
    strftime(${DB_FRACT_SEC_TIME}, datetime(mvf.prevLastDate, 'localtime')) || 
        printf('%+.2d:%.2d', round((julianday(mvf.prevLastDate, 'localtime') - julianday(mvf.prevLastDate)) * 24), 
            abs(round((julianday(mvf.prevLastDate, 'localtime') - julianday(mvf.prevLastDate)) * 24 * 60) % 60)) AS prevLastDate, 
    mvf.prevValue, p.isFavorite, 
    (SELECT vl.meterValue FROM ${MeterValueEntity.TABLE_NAME} vl
        WHERE vl.metersId = mvf.meterId
            AND strftime(${DB_FRACT_SEC_TIME}, vl.valueDate) = 
                    (SELECT MAX(strftime(${DB_FRACT_SEC_TIME}, v.valueDate)) 
                        FROM ${MeterValueEntity.TABLE_NAME} v
                        WHERE v.metersId = mvf.meterId
                            AND strftime(${DB_FRACT_SEC_TIME}, v.valueDate) > mpd.maxValueDate)
    ) AS currentValue,
    mvf.meterLocCode, sv.serviceLocCode, mvf.valueFormat
FROM (SELECT mps.meterValueId, mps.payersId, mps.servicesId, mps.meterId, mps.meterType, mps.meterMeasureUnit, 
            mps.prevLastDate, mps.prevValue, mps.meterLocCode,
            substr('#0.' || '0000000000', 1, 3 + (length(CAST(mps.maxValue / ${Constants.CONV_COEFF_BIGDECIMAL}.0 AS TEXT)) - 
                CASE WHEN instr(CAST(mps.maxValue / ${Constants.CONV_COEFF_BIGDECIMAL}.0 AS TEXT), '.') = 
                                        length(CAST(mps.maxValue / ${Constants.CONV_COEFF_BIGDECIMAL}.0 AS TEXT)) - 1
                    THEN length(CAST(mps.maxValue / ${Constants.CONV_COEFF_BIGDECIMAL}.0 AS TEXT)) + 1 
                    ELSE instr(CAST(mps.maxValue / ${Constants.CONV_COEFF_BIGDECIMAL}.0 AS TEXT), '.') 
                END)
            ) AS valueFormat 
        FROM (SELECT mvl.meterValueId, mv.payersId, mv.servicesId, mv.meterId, mv.meterType, mv.meterMeasureUnit, 
                    ifnull(strftime(${DB_FRACT_SEC_TIME}, mvl.valueDate),
                            strftime(${DB_FRACT_SEC_TIME}, 'now', 'localtime', 'start of month', '-1 days')) AS prevLastDate, 
                    mvl.meterValue AS prevValue, mv.meterLocCode, mv.maxValue
                FROM ${MeterPayerServiceView.VIEW_NAME} mv LEFT JOIN ${MeterValueEntity.TABLE_NAME} mvl ON mvl.metersId = mv.meterId
                    AND mv.isMeterOwner = $DB_TRUE
                UNION ALL
                SELECT NULL AS meterValueId, mv.payersId, mv.servicesId, mv.meterId, mv.meterType, mv.meterMeasureUnit,
                    strftime(${DB_FRACT_SEC_TIME}, mv.passportDate) AS prevLastDate,
                    mv.initValue AS prevValue, mv.meterLocCode, mv.maxValue
                FROM ${MeterPayerServiceView.VIEW_NAME} mv
                WHERE mv.passportDate IS NOT NULL AND mv.initValue IS NOT NULL AND mv.isMeterOwner = $DB_TRUE) mps) mvf
    JOIN ${ServiceView.VIEW_NAME} sv ON sv.serviceId = mvf.servicesId AND sv.serviceMeterType = mvf.meterType
    JOIN ${PayerEntity.TABLE_NAME} p ON p.payerId = mvf.payersId
    JOIN ${PayerServiceCrossRefEntity.TABLE_NAME} ps ON ps.servicesId = sv.serviceId AND ps.payersId = p.payerId AND ps.isMeterOwner = $DB_TRUE
    JOIN ${MeterValueMaxPrevDateView.VIEW_NAME} mpd ON mpd.meterId = mvf.meterId AND mpd.maxValueDate = mvf.prevLastDate
ORDER BY p.payerId, sv.servicePos
"""
)
class MeterValuePrevPeriodView(
    val meterValueId: UUID?,
    val payerId: UUID,
    val serviceId: UUID,
    val serviceType: ServiceType,
    val serviceName: String,
    val servicePos: Int,
    val meterId: UUID,
    val meterType: MeterType,
    val measureUnit: String?,
    @field:TypeConverters(DateTypeConverter::class)
    val prevLastDate: OffsetDateTime?,
    val prevValue: BigDecimal?,
    val isFavorite: Boolean,
    val currentValue: BigDecimal? = null,
    val meterLocCode: String,
    val serviceLocCode: String,
    val valueFormat: String
) {
    companion object {
        const val VIEW_NAME = "meter_value_prev_periods_view"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MeterValuePrevPeriodView
        meterValueId?.let { if (it != (other.meterValueId ?: "")) return false }
        if (meterId != other.meterId) return false

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
        return result  //(meterId.hashCode() << 1) + startMeterValue.hashCode()
    }

    override fun toString(): String {
        val str = StringBuffer()
        str.append("For '").append(serviceName).append("' Meter Prev Value: ").append(prevValue)
            .append(" ").append(measureUnit)
            .append(" by Period ")
            .append(if (prevLastDate != null) DateTimeFormatter.ISO_LOCAL_DATE.format(prevLastDate) else "...")
        currentValue?.let {
            str.append(" with current Value ").append(currentValue).append(" ").append(measureUnit)
        }
        str.append(" and value Format = ").append(valueFormat)
            .append(" [payerId = ").append(payerId)
            .append("; meterId = ").append(meterId)
            .append("] meterValueId = ").append(meterValueId)
        return str.toString()
    }
}