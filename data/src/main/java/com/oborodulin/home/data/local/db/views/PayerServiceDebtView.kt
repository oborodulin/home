package com.oborodulin.home.data.local.db.views

import androidx.room.DatabaseView
import com.oborodulin.home.common.util.Constants.CONV_COEFF_BIGDECIMAL
import com.oborodulin.home.data.local.db.entities.AppSettingEntity
import com.oborodulin.home.data.util.Constants
import com.oborodulin.home.data.util.ServiceType
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*

@DatabaseView(
    viewName = PayerServiceDebtView.VIEW_NAME,
    value = """
SELECT rps.payerId, rps.personsNum, rps.totalArea, rps.livingSpace, 
        mrv.paymentDate, 
        mrv.paymentMonth, mrv.paymentYear, rps.isPerPerson, rps.servicePos, rps.serviceType, rps.serviceName, 
        rps.serviceLocCode, rps.rateValue, rps.fromMeterValue, rps.toMeterValue, 
        NULL AS diffMeterValue, rps.serviceMeasureUnit AS measureUnit, 0 AS isDerivedUnit, rps.serviceId, 
        rps.payerServiceId,
        (CASE WHEN rps.isPerPerson = 1
            -- GAS, GARBAGE
            THEN rps.rateValue * rps.personsNum
            ELSE CASE WHEN rps.serviceType IN (${Constants.SRV_RENT_VAL}) THEN rps.rateValue * ifnull(rps.totalArea / ${CONV_COEFF_BIGDECIMAL}.0, 1)
                    WHEN rps.serviceType IN (${Constants.SRV_HEATING_VAL}) THEN rps.rateValue * ifnull(rps.livingSpace / ${CONV_COEFF_BIGDECIMAL}.0, 1)
                    -- DOORPHONE, PHONE, INTERNET, USGO
                    ELSE rps.rateValue
                END
        END) serviceDebt,
        rps.isMeterUses
FROM ${RatePayerServiceView.VIEW_NAME} rps JOIN 
        (SELECT rv.receiptId, rv.payersId, rv.payersServicesId, rv.isLinePaid, 
                mvp.paymentDate, mvp.paymentMonth, mvp.paymentYear, mvp.meterLocCode
            FROM ${MeterValuePaymentView.VIEW_NAME} mvp LEFT JOIN receipts_view rv 
                ON rv.payersId = mvp.payerId 
                AND rv.receiptMonth = mvp.paymentMonth 
                AND rv.receiptYear = mvp.paymentYear
                AND rv.payersServicesId <> mvp.payerServiceId
        GROUP BY rv.receiptId, rv.payersId, rv.payersServicesId, rv.isLinePaid, 
                mvp.paymentDate, mvp.paymentMonth, mvp.paymentYear, mvp.meterLocCode) mrv
            ON ifnull(mrv.payersServicesId, rps.payerServiceId) = rps.payerServiceId
                AND mrv.meterLocCode = rps.serviceLocCode
WHERE rps.isMeterUses = 0 -- тарифы по услугам без счётчиков
    AND strftime(${Constants.DB_FRACT_SEC_TIME}, rps.startDate) = ifnull(
                                                    (SELECT MAX(strftime(${Constants.DB_FRACT_SEC_TIME}, rsv.startDate)) 
                                                    FROM ${RatePayerServiceView.VIEW_NAME} rsv 
                                                    WHERE rsv.payerId = rps.payerId 
                                                        AND rsv.payerServiceId = rps.payerServiceId
                                                        AND rsv.serviceLocCode = rps.serviceLocCode
                                                        AND rsv.isMeterUses = 0
                                                        AND rsv.startDate <= mrv.paymentDate),
                                                    strftime(${Constants.DB_FRACT_SEC_TIME}, rps.startDate))
    AND ifnull(mrv.isLinePaid, 0) = 0
UNION ALL
SELECT psd.*, 
    printf('%02d', psd.paymentMonth) || '.' || CAST(psd.paymentYear AS TEXT) || ': ' ||
    (CASE WHEN psd.isDerivedUnit = 0
            THEN CAST(psd.rateMeterValue / ${CONV_COEFF_BIGDECIMAL}.0 AS TEXT) || ' ' || psd.measureUnit || ' x ' || 
                CAST(psd.rateValue / ${CONV_COEFF_BIGDECIMAL}.0 AS TEXT) || ' ' || asr.currency || ' = ' || 
                printf('%.2f', psd.serviceDebt / ${CONV_COEFF_BIGDECIMAL}.0) || ' ' || asr.currency
            ELSE CASE psd.serviceType 
                    WHEN ${Constants.SRV_HEATING_VAL} THEN 
                        CAST(psd.rateMeterValue / ${CONV_COEFF_BIGDECIMAL}.0 AS TEXT) || ' ' || psd.measureUnit || ' x ' || 
                        (CASE WHEN psd.livingSpace IS NOT NULL THEN CAST(psd.livingSpace / ${CONV_COEFF_BIGDECIMAL}.0 AS TEXT) || 
                            ' ' || asl.livingSpaceMu || ' x ' 
                            ELSE '' 
                        END) || 
                            CAST(psd.rateValue / ${CONV_COEFF_BIGDECIMAL}.0 AS TEXT) || ' ' || asr.currency || ' = ' || 
                            printf('%.2f', psd.serviceDebt / ${CONV_COEFF_BIGDECIMAL}.0) || ' ' || asr.currency
                    ELSE CAST(psd.rateValue / ${CONV_COEFF_BIGDECIMAL}.0 AS TEXT)
                END
    END) serviceDebtExpr
FROM (SELECT rps.payerId, rps.personsNum, rps.totalArea, rps.livingSpace, 
        mvp.paymentDate, 
        mvp.paymentMonth, mvp.paymentYear, 0 AS isPerPerson, rps.servicePos, rps.serviceType, rps.serviceName, 
        rps.serviceLocCode, rps.rateValue, rps.fromMeterValue, rps.toMeterValue, 
        (CASE WHEN mvp.diffMeterValue <= ifnull(rps.toMeterValue, mvp.diffMeterValue)
                THEN mvp.diffMeterValue - ifnull(rps.fromMeterValue, 0)
            ELSE ifnull(rps.toMeterValue, 0) - ifnull(rps.fromMeterValue, 0)
        END) AS rateMeterValue,
        mvp.diffMeterValue, mvp.measureUnit, 
        mvp.isDerivedUnit, rps.serviceId, rps.payerServiceId,
        (CASE WHEN mvp.isDerivedUnit = 0
            THEN rps.rateValue *         
                (CASE WHEN mvp.diffMeterValue <= ifnull(rps.toMeterValue, mvp.diffMeterValue)
                        THEN mvp.diffMeterValue - ifnull(rps.fromMeterValue, 0)
                    ELSE ifnull(rps.toMeterValue, 0) - ifnull(rps.fromMeterValue, 0)
                END) / ${CONV_COEFF_BIGDECIMAL}.0
            ELSE CASE rps.serviceType 
                    WHEN ${Constants.SRV_HEATING_VAL} THEN ifnull(rps.livingSpace / ${CONV_COEFF_BIGDECIMAL}.0, 1) * 
                                                                mvp.diffMeterValue / ${CONV_COEFF_BIGDECIMAL}.0 * rps.rateValue
                    ELSE rps.rateValue
                END
        END) AS serviceDebt,
        rps.isMeterUses
    FROM ${RatePayerServiceView.VIEW_NAME} rps JOIN ${MeterValuePaymentView.VIEW_NAME} mvp 
        ON mvp.payerId = rps.payerId AND mvp.payerServiceId = rps.payerServiceId
            AND mvp.meterLocCode = rps.serviceLocCode
            AND strftime(${Constants.DB_FRACT_SEC_TIME}, rps.startDate) = ifnull(
                                                    (SELECT MAX(strftime(${Constants.DB_FRACT_SEC_TIME}, rsv.startDate)) 
                                                    FROM ${RatePayerServiceView.VIEW_NAME} rsv 
                                                    WHERE rsv.payerId = rps.payerId 
                                                        AND rsv.payerServiceId = rps.payerServiceId
                                                        AND rsv.serviceLocCode = rps.serviceLocCode
                                                        AND rsv.isMeterUses = 1                 -- meters service rates
                                                        AND rsv.startDate <= mvp.paymentDate),
                                                    strftime(${Constants.DB_FRACT_SEC_TIME}, rps.startDate))
            AND mvp.diffMeterValue >= ifnull(rps.fromMeterValue, mvp.diffMeterValue)
        LEFT JOIN ${ReceiptView.VIEW_NAME} rv ON rv.receiptMonth = mvp.paymentMonth AND rv.receiptYear = mvp.paymentYear
                                AND rv.meterValuesId = mvp.meterValueId
    WHERE rps.isMeterUses = 1 AND ifnull(rv.isLinePaid, 0) = 0) psd,
    (SELECT paramValue AS currency FROM ${AppSettingEntity.TABLE_NAME} WHERE paramName = 'CURRENCY') asr
    (SELECT paramValue AS livingSpaceMu FROM ${AppSettingEntity.TABLE_NAME} WHERE paramName = 'LIVING_SPACE_MU') asl
"""
)
class PayerServiceDebtView(
    val payerId: UUID,
    val personsNum: Int,
    val totalArea: BigDecimal?,
    val livingSpace: BigDecimal?,
    val paymentDate: OffsetDateTime,
    val paymentMonth: Int,
    val paymentYear: Int,
    val isPerPerson: Boolean,
    val servicePos: Int,
    val serviceType: ServiceType,
    val serviceName: String,
    val serviceLocCode: String,
    val rateValue: BigDecimal,
    val fromMeterValue: BigDecimal?,
    val toMeterValue: BigDecimal?,
    val diffMeterValue: BigDecimal?,
    val measureUnit: String?,
    val isDerivedUnit: Boolean,
    val serviceId: UUID,
    val payerServiceId: UUID,
    val serviceDebt: BigDecimal,
    val isMeterUses: Boolean,
    val serviceDebtExpr: String?
) {
    companion object {
        const val VIEW_NAME = "payer_service_debts_view"
    }
}