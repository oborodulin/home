package com.oborodulin.home.data.local.db.views

import androidx.room.DatabaseView
import androidx.room.TypeConverters
import com.oborodulin.home.common.util.Constants
import com.oborodulin.home.data.local.db.converters.DateTypeConverter
import com.oborodulin.home.data.local.db.entities.MeterValueEntity
import com.oborodulin.home.data.local.db.entities.PayerEntity
import com.oborodulin.home.data.util.Constants.DB_FRACT_SEC_TIME
import com.oborodulin.home.data.util.ServiceType
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*

@DatabaseView(
    viewName = MeterValuePrevPeriodView.VIEW_NAME,
    value = """
SELECT mvf.meterValueId, p.payerId, sv.serviceId, sv.serviceType, sv.serviceName, sv.servicePos, 
    mvf.meterId, IFNULL(mvf.measureUnit, sv.measureUnit) AS measureUnit,
    mvf.prevLastDate, mvf.prevValue, p.isFavorite, 
    (SELECT vl.meterValue FROM meter_values vl
        WHERE vl.metersId = mvf.meterId
            AND strftime(${DB_FRACT_SEC_TIME}, vl.valueDate) = 
                    (SELECT MAX(strftime(${DB_FRACT_SEC_TIME}, v.valueDate)) FROM meter_values v
                        WHERE v.metersId = mvf.meterId
                            AND strftime(${DB_FRACT_SEC_TIME}, v.valueDate) > mpd.maxValueDate)
    ) AS currentValue,
    mvf.meterLocaleCode, sv.localeCode AS serviceLocaleCode,
    mvf.valueFormat
FROM (SELECT mvl.meterValueId, mv.payersId, mv.servicesId, mv.meterId, mv.meterType, mv.measureUnit, 
        IFNULL(strftime(${DB_FRACT_SEC_TIME}, mvl.valueDate), 
                strftime(${DB_FRACT_SEC_TIME}, mv.passportDate, 'start of month', '-1 days')) AS prevLastDate, 
        IFNULL(mvl.meterValue, mv.initValue) AS prevValue, mv.localeCode AS meterLocaleCode,
        substr('#0.' || '0000000000', 1, 3 + (length(cast(mv.maxValue / ${Constants.CONV_COEFF_BIGDECIMAL}.0 as text)) - 
            CASE WHEN instr(cast(mv.maxValue / ${Constants.CONV_COEFF_BIGDECIMAL}.0 as text), '.') = 
                                    length(cast(mv.maxValue / ${Constants.CONV_COEFF_BIGDECIMAL}.0 as text)) - 1
                THEN length(cast(mv.maxValue / ${Constants.CONV_COEFF_BIGDECIMAL}.0 as text)) + 1 
                ELSE instr(cast(mv.maxValue / ${Constants.CONV_COEFF_BIGDECIMAL}.0 as text), '.') 
            END)
        ) AS valueFormat 
    FROM ${MeterView.VIEW_NAME} mv LEFT JOIN ${MeterValueEntity.TABLE_NAME} mvl ON mvl.metersId = mv.meterId) mvf
        JOIN ${ServiceView.VIEW_NAME} sv ON sv.serviceId = mvf.servicesId AND sv.meterType = mvf.meterType
        JOIN ${PayerEntity.TABLE_NAME} p ON p.payerId = mvf.payersId
        JOIN ${MeterValueMaxPrevDateView.VIEW_NAME} mpd ON mpd.meterId = mvf.meterId AND mpd.maxValueDate = mvf.prevLastDate
"""
)
class MeterValuePrevPeriodView(
    val meterValueId: UUID,
    val payerId: UUID,
    val serviceId: UUID,
    val serviceType: ServiceType,
    val serviceName: String,
    val servicePos: Int,
    val meterId: UUID,
    val measureUnit: String?,
    @field:TypeConverters(DateTypeConverter::class)
    val prevLastDate: OffsetDateTime,
    val prevValue: BigDecimal?,
    val isFavorite: Boolean,
    val currentValue: BigDecimal? = null,
    val meterLocaleCode: String,
    val serviceLocaleCode: String,
    val valueFormat: String
) {
    companion object {
        const val VIEW_NAME = "meter_value_prev_periods_view"
    }
}