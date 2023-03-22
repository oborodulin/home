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
SELECT psd.*, 
    printf('%02d.%d: ', psd.paymentMonth, psd.paymentYear) ||
    (CASE WHEN psd.isPerPerson = 1
            -- GAS, GARBAGE
            THEN printf(${Constants.FMT_IS_PER_PERSON_EXPR}, psd.personsNum, psd.personMu, 
                            psd.rateValue / ${CONV_COEFF_BIGDECIMAL}.0, psd.currency, psd.serviceDebt / ${CONV_COEFF_BIGDECIMAL}.0, psd.currency)
            ELSE CASE 0
                    WHEN psd.serviceType IN (${Constants.SRV_RENT_VAL}) THEN 
                        (CASE WHEN psd.totalArea IS NOT NULL    
                            THEN printf('%.2f %s x ', psd.totalArea / ${CONV_COEFF_BIGDECIMAL}.0, psd.totalAreaMu) 
                            ELSE '' 
                        END) || 
                            printf('%.2f %s = %.2f %s', psd.rateValue / ${CONV_COEFF_BIGDECIMAL}.0, psd.currency, 
                                                            psd.serviceDebt / ${CONV_COEFF_BIGDECIMAL}.0, psd.currency)                        
                    WHEN psd.serviceType IN (${Constants.SRV_HEATING_VAL}) THEN 
                        (CASE WHEN psd.livingSpace IS NOT NULL 
                            THEN printf('%.2f %s x ', psd.livingSpace / ${CONV_COEFF_BIGDECIMAL}.0, psd.livingSpaceMu) 
                            ELSE '' 
                        END) || 
                            printf('%.2f %s = %.2f %s', psd.rateValue / ${CONV_COEFF_BIGDECIMAL}.0, psd.currency, 
                                                            psd.serviceDebt / ${CONV_COEFF_BIGDECIMAL}.0, psd.currency)                        
                    -- DOORPHONE, PHONE, INTERNET, USGO
                    ELSE printf(${Constants.FMT_DEBT_EXPR}, psd.serviceDebt / ${CONV_COEFF_BIGDECIMAL}.0, psd.currency)
                END
    END) serviceDebtExpr
FROM (SELECT rps.payerId, rps.personsNum, rps.totalArea, rps.livingSpace, 
        mrv.paymentDate, 
        mrv.paymentMonth, mrv.paymentYear, rps.isPerPerson, rps.servicePos, rps.serviceType, rps.serviceName, 
        rps.serviceLocCode, rps.rateValue, rps.fromMeterValue, rps.toMeterValue, NULL AS rateMeterValue,
        NULL AS diffMeterValue, rps.serviceMeasureUnit AS measureUnit, 0 AS isDerivedUnit, rps.serviceId, 
        rps.payerServiceId,
        (CASE WHEN rps.isPerPerson = 1
            -- GAS, GARBAGE
            THEN rps.personsNum * rps.rateValue
            ELSE CASE WHEN rps.serviceType IN (${Constants.SRV_RENT_VAL}) THEN ifnull(rps.totalArea / ${CONV_COEFF_BIGDECIMAL}.0, 1) * rps.rateValue
                    WHEN rps.serviceType IN (${Constants.SRV_HEATING_VAL}) THEN ifnull(rps.livingSpace / ${CONV_COEFF_BIGDECIMAL}.0, 1) * rps.rateValue
                    -- DOORPHONE, PHONE, INTERNET, USGO
                    ELSE rps.rateValue
                END
        END) serviceDebt,
        rps.isMeterUses,
        (SELECT paramValue FROM ${AppSettingEntity.TABLE_NAME} WHERE paramName = ${Constants.PRM_PERSON_NUM_MU_VAL}) AS personMu,
        (SELECT paramValue FROM ${AppSettingEntity.TABLE_NAME} WHERE paramName = ${Constants.PRM_CURRENCY_VAL}) AS currency,
        (SELECT paramValue FROM ${AppSettingEntity.TABLE_NAME} WHERE paramName = ${Constants.PRM_TOTAL_AREA_MU_VAL}) AS totalAreaMu,
        (SELECT paramValue FROM ${AppSettingEntity.TABLE_NAME} WHERE paramName = ${Constants.PRM_LIVING_SPACE_MU_VAL}) AS livingSpaceMu
    FROM ${RatePayerServiceView.VIEW_NAME} rps LEFT JOIN 
            (SELECT rv.receiptId, rv.payersId, rv.payersServicesId, rv.isLinePaid, 
                    mvp.paymentDate, mvp.paymentMonth, mvp.paymentYear, mvp.meterLocCode
                FROM ${MeterValuePaymentView.VIEW_NAME} mvp LEFT JOIN ${ReceiptView.VIEW_NAME} rv 
                    ON rv.payersId = mvp.payerId 
                    AND rv.receiptMonth = mvp.paymentMonth 
                    AND rv.receiptYear = mvp.paymentYear
                    AND rv.payersServicesId <> mvp.payerServiceId
            GROUP BY rv.receiptId, rv.payersId, rv.payersServicesId, rv.isLinePaid, 
                    mvp.paymentDate, mvp.paymentMonth, mvp.paymentYear, mvp.meterLocCode) mrv
                ON ifnull(mrv.payersServicesId, rps.payerServiceId) = rps.payerServiceId
                    AND ifnull(mrv.isLinePaid, 0) = 0 AND mrv.meterLocCode = rps.serviceLocCode
    WHERE rps.isMeterUses = 0                                                           -- rates for services without meters
        AND strftime(${Constants.DB_FRACT_SEC_TIME}, rps.startDate) = ifnull(
                                                    (SELECT MAX(strftime(${Constants.DB_FRACT_SEC_TIME}, rsv.startDate)) 
                                                    FROM ${RatePayerServiceView.VIEW_NAME} rsv 
                                                    WHERE rsv.payerId = rps.payerId 
                                                        AND rsv.payerServiceId = rps.payerServiceId
                                                        AND rsv.serviceLocCode = rps.serviceLocCode
                                                        AND rsv.isMeterUses = 0
                                                        AND strftime(${Constants.DB_FRACT_SEC_TIME}, rsv.startDate) <= ifnull(strftime(${Constants.DB_FRACT_SEC_TIME}, rps.fromServiceDate), 
                                                                                                                            strftime(${Constants.DB_FRACT_SEC_TIME}, mrv.paymentDate))),
                                                    strftime(${Constants.DB_FRACT_SEC_TIME}, rps.startDate))) psd
UNION ALL
SELECT psd.*, 
    printf('%02d.%d: ', psd.paymentMonth, psd.paymentYear) ||
    (CASE WHEN psd.isDerivedUnit = 0
            THEN printf(${Constants.FMT_METER_VAL_EXPR}, psd.rateMeterValue / ${CONV_COEFF_BIGDECIMAL}.0, psd.measureUnit,
                                                        psd.rateValue / ${CONV_COEFF_BIGDECIMAL}.0, psd.currency, 
                                                        psd.serviceDebt / ${CONV_COEFF_BIGDECIMAL}.0, psd.currency)                        
            ELSE CASE psd.serviceType 
                    WHEN ${Constants.SRV_HEATING_VAL} THEN 
                        printf('%.5f %s x ', psd.rateMeterValue / ${CONV_COEFF_BIGDECIMAL}.0, psd.measureUnit) || 
                        (CASE WHEN psd.livingSpace IS NOT NULL 
                            THEN printf('%.2f %s x ', psd.livingSpace / ${CONV_COEFF_BIGDECIMAL}.0,  psd.livingSpaceMu) 
                            ELSE '' 
                        END) || 
                            printf('%.2f %s = %.2f %s', psd.rateValue / ${CONV_COEFF_BIGDECIMAL}.0, psd.currency, 
                                                        psd.serviceDebt / ${CONV_COEFF_BIGDECIMAL}.0, psd.currency)                        
                    ELSE printf(${Constants.FMT_DEBT_EXPR}, psd.serviceDebt / ${CONV_COEFF_BIGDECIMAL}.0, psd.currency)
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
        rps.isMeterUses,
        NULL AS personMu,
        (SELECT paramValue FROM ${AppSettingEntity.TABLE_NAME} WHERE paramName = ${Constants.PRM_CURRENCY_VAL}) AS currency,
        NULL AS totalAreaMu,
        (SELECT paramValue FROM ${AppSettingEntity.TABLE_NAME} WHERE paramName = ${Constants.PRM_LIVING_SPACE_MU_VAL}) AS livingSpaceMu        
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
                                                        AND strftime(${Constants.DB_FRACT_SEC_TIME}, rsv.startDate) <= strftime(${Constants.DB_FRACT_SEC_TIME}, mvp.paymentDate)),
                                                    strftime(${Constants.DB_FRACT_SEC_TIME}, rps.startDate))
            AND mvp.diffMeterValue >= ifnull(rps.fromMeterValue, mvp.diffMeterValue)
        LEFT JOIN ${ReceiptView.VIEW_NAME} rv ON rv.receiptMonth = mvp.paymentMonth AND rv.receiptYear = mvp.paymentYear
                                AND rv.meterValuesId = mvp.meterValueId
    WHERE rps.isMeterUses = 1 AND ifnull(rv.isLinePaid, 0) = 0) psd
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
    val personMu: String?,
    val currency: String?,
    val totalAreaMu: String?,
    val livingSpaceMu: String?,
    val serviceDebtExpr: String?
) {
    companion object {
        const val VIEW_NAME = "payer_service_debts_view"
    }
}