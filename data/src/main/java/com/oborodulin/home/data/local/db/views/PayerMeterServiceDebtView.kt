package com.oborodulin.home.data.local.db.views

import androidx.room.DatabaseView
import com.oborodulin.home.common.util.Constants.CONV_COEFF_BIGDECIMAL
import com.oborodulin.home.data.local.db.entities.AppSettingEntity
import com.oborodulin.home.data.util.Constants
import com.oborodulin.home.data.util.Constants.DB_FALSE
import com.oborodulin.home.data.util.Constants.DB_TRUE
import com.oborodulin.home.data.util.ServiceType
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*

@DatabaseView(
    viewName = PayerMeterServiceDebtView.VIEW_NAME,
    value = """
SELECT pms.payerId, pms.fromPaymentDate, pms.toPaymentDate, pms.fullMonths, 
    pms.serviceId, pms.payerServiceId, pms.servicePos, pms.serviceType, pms.serviceName, pms.serviceLocCode,  
    pms.startMeterValue, pms.endMeterValue, pms.diffMeterValue, pms.measureUnit, pms.isMeterUses, 
    pms.debt AS serviceDebt, 
    printf(${Constants.FMT_PAYMENT_PERIOD_EXPR}, pms.paymentMonth, pms.paymentYear) ||
    (CASE WHEN pms.isDerivedUnit = $DB_FALSE
            THEN printf(${Constants.FMT_METER_VAL_EXPR}, pms.rateMeterValue / ${CONV_COEFF_BIGDECIMAL}.0, pms.measureUnit,
                                                        pms.rateValue / ${CONV_COEFF_BIGDECIMAL}.0, pms.currencyCode, 
                                                        pms.debt / ${CONV_COEFF_BIGDECIMAL}.0, pms.currencyCode)                        
            ELSE CASE pms.serviceType 
                    WHEN ${Constants.SRV_HEATING_VAL} THEN 
                        printf(${Constants.FMT_HEATING_METER_EXPR}, pms.rateMeterValue / ${CONV_COEFF_BIGDECIMAL}.0, pms.measureUnit) || 
                        (CASE WHEN pms.livingSpace IS NOT NULL 
                            THEN printf(${Constants.FMT_OPT_FACTOR_EXPR}, pms.livingSpace / ${CONV_COEFF_BIGDECIMAL}.0,  pms.livingSpaceMu) 
                            ELSE '' 
                        END) || 
                            printf(${Constants.FMT_RATE_DEBT_EXPR}, pms.rateValue / ${CONV_COEFF_BIGDECIMAL}.0, pms.currencyCode, pms.debt / ${CONV_COEFF_BIGDECIMAL}.0, pms.currencyCode)                        
                    ELSE printf(${Constants.FMT_DEBT_EXPR}, pms.debt / ${CONV_COEFF_BIGDECIMAL}.0, pms.currencyCode)
                END
    END) serviceDebtExpr
FROM (SELECT psd.payerId, psd.meterId, psd.fromPaymentDate, psd.toPaymentDate, psd.fullMonths, 
        psd.paymentMonth, psd.paymentYear, psd.livingSpace,
        psd.serviceId, psd.payerServiceId, psd.servicePos, psd.serviceType, psd.serviceName, psd.serviceLocCode, 
        psd.startMeterValue, psd.endMeterValue, psd.diffMeterValue, psd.measureUnit, psd.isDerivedUnit, psd.isMeterUses,        
        psd.currRateMonths, psd.currRateStartDate, psd.rateValue, 
        psd.prevRateMonths, psd.prevRateStartDate, psd.prevRateValue,
        psd.rateMeterValue,
        ifnull(CASE WHEN ifnull(psd.prevRateMonths, 0) > 0 
                    THEN (psd.prevRateMonths - 1) * psd.rateMeterValue / ${CONV_COEFF_BIGDECIMAL}.0 * psd.prevRateValue + 
                        (psd.currRateMonths + 1) * psd.rateMeterValue / ${CONV_COEFF_BIGDECIMAL}.0 * psd.rateValue
                    ELSE CASE WHEN ifnull(psd.fullMonths, 1) > 1
                            THEN (psd.fullMonths - 1) * psd.debt
                            ELSE psd.debt
                        END
                END, 0) AS debt,
        psd.currencyCode, psd.livingSpaceMu
    FROM (SELECT mvp.payerId, mvp.meterId, crp.livingSpace, mvp.fromPaymentDate, mvp.toPaymentDate, mvp.paymentMonth, mvp.paymentYear,
            crp.serviceId, crp.payerServiceId, crp.servicePos, crp.serviceType, crp.serviceName, crp.serviceLocCode, 
            crp.startDate AS currRateStartDate, crp.rateValue, NULL AS nextRateStartDate,
            mvp.startMeterValue, mvp.endMeterValue, 
            (CASE WHEN mvp.diffMeterValue <= ifnull(crp.toMeterValue, mvp.diffMeterValue)
                    THEN mvp.diffMeterValue - ifnull(crp.fromMeterValue, 0)
                ELSE ifnull(crp.toMeterValue, 0) - ifnull(crp.fromMeterValue, 0)
            END) AS rateMeterValue,
            mvp.diffMeterValue, mvp.measureUnit, mvp.isDerivedUnit,
            (CASE WHEN mvp.isDerivedUnit = $DB_FALSE
                THEN (CASE WHEN mvp.diffMeterValue <= ifnull(crp.toMeterValue, mvp.diffMeterValue)
                            THEN mvp.diffMeterValue - ifnull(crp.fromMeterValue, 0)
                        ELSE ifnull(crp.toMeterValue, 0) - ifnull(crp.fromMeterValue, 0)
                    END) / ${CONV_COEFF_BIGDECIMAL}.0 * ifnull(crp.rateValue, 0) -- ifnull(rateValue...) if startDate > toPaymentDate
                ELSE CASE ifnull(crp.serviceType, "") 
                        WHEN ${Constants.SRV_HEATING_VAL} 
                            THEN ifnull(crp.livingSpace / ${CONV_COEFF_BIGDECIMAL}.0, 1) * mvp.diffMeterValue / ${CONV_COEFF_BIGDECIMAL}.0 * ifnull(crp.rateValue, 0)
                        ELSE ifnull(crp.rateValue, 0)
                    END
            END) AS debt,
            mvp.diffMonths AS fullMonths, crp.isMeterUses,
            (strftime('%Y', ifnull(mvp.toPaymentDate, datetime('now', 'localtime')), 'start of month', '-1 day') * 12 + 
                strftime('%m', ifnull(mvp.toPaymentDate, datetime('now', 'localtime')), 'start of month', '-1 day') -
            strftime('%Y', crp.startDate) * 12 - strftime('%m', crp.startDate) +
                (strftime('%d', ifnull(mvp.toPaymentDate, datetime('now', 'localtime')), '+1 day') = '01' OR 
                strftime('%d', ifnull(mvp.toPaymentDate, datetime('now', 'localtime'))) >= strftime('%d', crp.startDate))) AS currRateMonths,
            (strftime('%Y', ifnull(crp.startDate, datetime('now', 'localtime')), 'start of month', '-1 day') * 12 + 
                strftime('%m', ifnull(crp.startDate, datetime('now', 'localtime')), 'start of month', '-1 day') -
            strftime('%Y', mvp.fromPaymentDate) * 12 - strftime('%m', mvp.fromPaymentDate) +
                (strftime('%d', ifnull(crp.startDate, datetime('now', 'localtime')), '+1 day') = '01' OR 
                strftime('%d', ifnull(crp.startDate, datetime('now', 'localtime'))) >= strftime('%d', mvp.fromPaymentDate))) AS prevRateMonths,
            prp.startDate AS prevRateStartDate, prp.rateValue AS prevRateValue,
            (SELECT COUNT(rv.receiptLineId) FROM ${ReceiptView.VIEW_NAME}  rv
                WHERE rv.payersId = mvp.payerId AND rv.receiptDate > mvp.fromPaymentDate AND rv.receiptDate <= mvp.toPaymentDate 
                    AND rv.meterValuesId = mvp.meterValueId AND rv.isLinePaid = $DB_TRUE) paidMonths,
            (SELECT paramValue FROM ${AppSettingEntity.TABLE_NAME} WHERE paramName = ${Constants.PRM_CURRENCY_CODE_VAL}) AS currencyCode,
            (SELECT paramValue FROM ${AppSettingEntity.TABLE_NAME} WHERE paramName = ${Constants.PRM_LIVING_SPACE_MU_VAL}) AS livingSpaceMu        
        FROM ${MeterValuePaymentView.VIEW_NAME} mvp LEFT JOIN ${RatePayerServiceView.VIEW_NAME} crp
            ON crp.isMeterUses = $DB_TRUE AND crp.payerId = mvp.payerId AND crp.payerServiceId = mvp.payerServiceId
                AND crp.serviceLocCode = mvp.meterLocCode
                AND ifnull(crp.fromMeterValue, mvp.diffMeterValue) <= mvp.diffMeterValue
                AND strftime(${Constants.DB_DAY_TIME}, crp.startDate) =
                                                    (SELECT MAX(strftime(${Constants.DB_DAY_TIME}, rsv.startDate)) 
                                                    FROM ${RatePayerServiceView.VIEW_NAME} rsv 
                                                    WHERE rsv.payerId = crp.payerId AND rsv.payerServiceId = crp.payerServiceId
                                                        AND rsv.serviceLocCode = crp.serviceLocCode
                                                        AND rsv.isMeterUses = $DB_TRUE
                                                        AND ifnull(rsv.fromMeterValue, mvp.diffMeterValue) <= mvp.diffMeterValue
                                                        AND strftime(${Constants.DB_FRACT_SEC_TIME}, rsv.startDate) <= strftime(${Constants.DB_FRACT_SEC_TIME}, mvp.toPaymentDate))
            LEFT JOIN ${RatePayerServiceView.VIEW_NAME} prp
                ON prp.isMeterUses = $DB_TRUE AND prp.payerId = crp.payerId AND prp.payerServiceId = crp.payerServiceId
                    AND prp.serviceLocCode = crp.serviceLocCode
                    AND ifnull(prp.fromMeterValue, -1) = ifnull(crp.fromMeterValue, -1)
                    AND mvp.diffMonths > 1 AND strftime(${Constants.DB_FRACT_SEC_TIME}, crp.startDate) > strftime(${Constants.DB_FRACT_SEC_TIME}, mvp.fromPaymentDate)
                    AND strftime(${Constants.DB_DAY_TIME}, prp.startDate) =
                                                    (SELECT MAX(strftime(${Constants.DB_DAY_TIME}, rsv.startDate)) 
                                                    FROM ${RatePayerServiceView.VIEW_NAME} rsv 
                                                    WHERE rsv.payerId = prp.payerId AND rsv.payerServiceId = prp.payerServiceId
                                                        AND rsv.serviceLocCode = prp.serviceLocCode
                                                        AND rsv.isMeterUses = $DB_TRUE
                                                        AND ifnull(rsv.fromMeterValue, -1) = ifnull(prp.fromMeterValue, -1)
                                                        AND mvp.diffMonths > 1 
                                                        AND strftime(${Constants.DB_FRACT_SEC_TIME}, crp.startDate) > strftime(${Constants.DB_FRACT_SEC_TIME}, mvp.fromPaymentDate)
                                                        AND strftime(${Constants.DB_FRACT_SEC_TIME}, rsv.startDate) < strftime(${Constants.DB_FRACT_SEC_TIME}, crp.startDate))
            LEFT JOIN ${ReceiptView.VIEW_NAME} rv ON rv.payersId = mvp.payerId AND rv.receiptDate > mvp.fromPaymentDate AND rv.receiptDate <= mvp.toPaymentDate 
                                                    AND rv.meterValuesId = mvp.meterValueId
    WHERE mvp.toPaymentDate IS NOT NULL -- if last meter value
        AND crp.serviceId IS NOT NULL -- if startDate > toPaymentDate 
        AND ifnull(rv.isLinePaid, 0) = $DB_FALSE) psd) pms
"""
)
class PayerMeterServiceDebtView(
    val payerId: UUID,
    val fromPaymentDate: OffsetDateTime,
    val toPaymentDate: OffsetDateTime,
    val fullMonths: Int,
    val serviceId: UUID,
    val payerServiceId: UUID,
    val servicePos: Int,
    val serviceType: ServiceType,
    val serviceName: String,
    val serviceLocCode: String,
    val startMeterValue: BigDecimal?,
    val endMeterValue: BigDecimal?,
    val diffMeterValue: BigDecimal?,
    val measureUnit: String?,
    val isMeterUses: Boolean,
    val serviceDebt: BigDecimal,
    val serviceDebtExpr: String?
) {
    companion object {
        const val VIEW_NAME = "payer_meter_service_debts_view"
    }
}