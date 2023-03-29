package com.oborodulin.home.data.local.db.views

import androidx.room.DatabaseView
import androidx.room.TypeConverters
import com.oborodulin.home.common.util.Constants.CONV_COEFF_BIGDECIMAL
import com.oborodulin.home.data.local.db.converters.DateTypeConverter
import com.oborodulin.home.data.local.db.entities.AppSettingEntity
import com.oborodulin.home.data.util.Constants
import com.oborodulin.home.data.util.Constants.DB_FALSE
import com.oborodulin.home.data.util.Constants.DB_TRUE
import com.oborodulin.home.data.util.ServiceType
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*

@DatabaseView(
    viewName = PayerServiceDebtView.VIEW_NAME,
    value = """
SELECT psd.payerId, psd.fromPaymentDate, psd.toPaymentDate, psd.fullMonths, 
    psd.serviceId, psd.payerServiceId, psd.servicePos, psd.serviceType, psd.serviceName, psd.serviceLocCode, 
    psd.fromMeterValue, psd.toMeterValue, psd.diffMeterValue, psd.measureUnit, psd.isMeterUses, 
    (ifnull(psd.fullMonths, 1) * psd.debt) AS serviceDebt,
    printf(${Constants.FMT_PAYMENT_PERIOD_EXPR}, psd.paymentMonth, psd.paymentYear) ||
    (CASE WHEN ifnull(psd.fullMonths, 1) > 1 THEN printf('%d %s x ', psd.fullMonths, psd.monthMu) ELSE '' END) ||
    (CASE WHEN psd.isPerPerson = $DB_TRUE
            -- GAS, GARBAGE
            THEN printf(${Constants.FMT_IS_PER_PERSON_EXPR}, psd.personsNum, psd.personMu, 
                            psd.rateValue / ${CONV_COEFF_BIGDECIMAL}.0, psd.currencyCode, ifnull(psd.fullMonths, 1) * psd.debt / ${CONV_COEFF_BIGDECIMAL}.0, psd.currencyCode)
            ELSE CASE 
                    -- RENT
                    WHEN psd.serviceType IN (${Constants.SRV_RENT_VAL}) THEN 
                        (CASE WHEN psd.totalArea IS NOT NULL    
                            THEN printf(${Constants.FMT_OPT_FACTOR_EXPR}, psd.totalArea / ${CONV_COEFF_BIGDECIMAL}.0, psd.totalAreaMu) 
                            ELSE '' 
                        END) || 
                            printf(${Constants.FMT_RATE_DEBT_EXPR}, psd.rateValue / ${CONV_COEFF_BIGDECIMAL}.0, psd.currencyCode, 
                                            ifnull(psd.fullMonths, 1) * psd.debt / ${CONV_COEFF_BIGDECIMAL}.0, psd.currencyCode)                        
                    -- HEATING
                    WHEN psd.serviceType IN (${Constants.SRV_HEATING_VAL}) THEN 
                        (CASE WHEN psd.livingSpace IS NOT NULL 
                            THEN printf(${Constants.FMT_OPT_FACTOR_EXPR}, psd.livingSpace / ${CONV_COEFF_BIGDECIMAL}.0, psd.livingSpaceMu) 
                            ELSE '' 
                        END) || 
                            printf(${Constants.FMT_RATE_DEBT_EXPR}, psd.rateValue / ${CONV_COEFF_BIGDECIMAL}.0, psd.currencyCode, 
                                            ifnull(psd.fullMonths, 1) * psd.debt / ${CONV_COEFF_BIGDECIMAL}.0, psd.currencyCode)                        
                    -- DOORPHONE, PHONE, INTERNET, USGO
                    ELSE printf(${Constants.FMT_DEBT_EXPR}, ifnull(psd.fullMonths, 1) * psd.debt / ${CONV_COEFF_BIGDECIMAL}.0, psd.currencyCode)
                END
    END) serviceDebtExpr
FROM (SELECT rps.payerId, rps.personsNum, rps.totalArea, rps.livingSpace,
        (CASE WHEN rps.isMeterUses = $DB_FALSE AND rps.fromServiceDate IS NOT NULL 
            THEN CASE WHEN julianday(rps.fromServiceDate) - julianday(rps.startDate) > 0
                    THEN rps.fromServiceDate
                    ELSE rps.startDate
                END
            ELSE ifnull(mrv.fromPaymentDate, strftime(${Constants.DB_FRACT_SEC_TIME}, 'now', 'localtime', 'start of month'))
        END) AS fromPaymentDate, 
        (CASE WHEN rps.isMeterUses = $DB_FALSE AND rps.fromServiceDate IS NOT NULL 
            THEN ifnull(psl.startDate, datetime('now', 'localtime'))
            ELSE ifnull(mrv.toPaymentDate, strftime(${Constants.DB_FRACT_SEC_TIME}, 'now', 'localtime', 'start of month'))
        END) AS toPaymentDate, 
        ifnull(rps.fromServiceMonth, ifnull(mrv.paymentMonth, CAST(strftime('%m', strftime(${Constants.DB_FRACT_SEC_TIME}, 'now', 'localtime', 'start of month')) AS INTEGER))) AS paymentMonth, 
        ifnull(rps.fromServiceYear, ifnull(mrv.paymentYear, CAST(strftime('%Y', strftime(${Constants.DB_FRACT_SEC_TIME}, 'now', 'localtime', 'start of month')) AS INTEGER))) AS paymentYear, 
        rps.isPerPerson, rps.servicePos, rps.serviceType, rps.serviceName, rps.fromServiceDate,
        rps.serviceLocCode, rps.rateValue, psl.startDate AS nextRateStartDate, 
        rps.fromMeterValue, rps.toMeterValue, NULL AS rateMeterValue, NULL AS diffMeterValue, 
        rps.serviceMeasureUnit AS measureUnit, 0 AS isDerivedUnit, rps.serviceId, rps.payerServiceId,
        (CASE WHEN rps.isPerPerson = $DB_TRUE
            --! GAS, GARBAGE
            THEN rps.personsNum * rps.rateValue
            ELSE CASE 
                    --! RENT
                    WHEN rps.serviceType IN (${Constants.SRV_RENT_VAL}) THEN ifnull(rps.totalArea / ${CONV_COEFF_BIGDECIMAL}.0, 1) * rps.rateValue
                    --! HEATING
                    WHEN rps.serviceType IN (${Constants.SRV_HEATING_VAL}) THEN ifnull(rps.livingSpace / ${CONV_COEFF_BIGDECIMAL}.0, 1) * rps.rateValue
                    --! DOORPHONE, PHONE, INTERNET, USGO
                    ELSE rps.rateValue
                END
        END) debt,
        (CASE WHEN rps.isMeterUses = $DB_FALSE AND rps.fromServiceDate IS NOT NULL 
            THEN (CASE 
                    WHEN julianday(rps.fromServiceDate) - julianday(rps.startDate) > 0
                    THEN strftime('%Y', ifnull(psl.startDate, datetime('now', 'localtime')), 'start of month', '-1 day') * 12 + 
                                strftime('%m', ifnull(psl.startDate, datetime('now', 'localtime')), 'start of month', '-1 day') -
                            strftime('%Y', rps.fromServiceDate) * 12 - strftime('%m', rps.fromServiceDate) +
                                (strftime('%d', ifnull(psl.startDate, datetime('now', 'localtime')), '+1 day') = '01' OR 
                                strftime('%d', ifnull(psl.startDate, datetime('now', 'localtime'))) >= strftime('%d', rps.fromServiceDate))
                    ELSE strftime('%Y', ifnull(psl.startDate, datetime('now', 'localtime')), 'start of month', '-1 day') * 12 + 
                                strftime('%m', ifnull(psl.startDate, datetime('now', 'localtime')), 'start of month', '-1 day') -
                            strftime('%Y', rps.startDate) * 12 - strftime('%m', rps.startDate) +
                                (strftime('%d', ifnull(psl.startDate, datetime('now', 'localtime')), '+1 day') = '01' OR 
                                strftime('%d', ifnull(psl.startDate, datetime('now', 'localtime'))) >= strftime('%d', rps.startDate))
                END)
            ELSE 1
        END) AS fullMonths,        
        rps.isMeterUses,
        (SELECT paramValue FROM ${AppSettingEntity.TABLE_NAME} WHERE paramName = ${Constants.PRM_PERSON_NUM_MU_VAL}) AS personMu,
        (SELECT paramValue FROM ${AppSettingEntity.TABLE_NAME} WHERE paramName = ${Constants.PRM_CURRENCY_CODE_VAL}) AS currencyCode,
        (SELECT paramValue FROM ${AppSettingEntity.TABLE_NAME} WHERE paramName = ${Constants.PRM_MONTH_MU_VAL}) AS monthMu,
        (SELECT paramValue FROM ${AppSettingEntity.TABLE_NAME} WHERE paramName = ${Constants.PRM_TOTAL_AREA_MU_VAL}) AS totalAreaMu,
        (SELECT paramValue FROM ${AppSettingEntity.TABLE_NAME} WHERE paramName = ${Constants.PRM_LIVING_SPACE_MU_VAL}) AS livingSpaceMu
    FROM ${RatePayerServiceView.VIEW_NAME} rps LEFT JOIN 
        -- payer services without fromServiceDate controls by meters payment info anf receipts from another services
            (SELECT rv.receiptId, rv.payersId, rv.payersServicesId, rv.isLinePaid, 
                    mvp.fromPaymentDate,  mvp.toPaymentDate, mvp.paymentMonth, mvp.paymentYear, 
                    mvp.meterLocCode
            FROM ${MeterValuePaymentView.VIEW_NAME} mvp LEFT JOIN ${ReceiptView.VIEW_NAME} rv 
                ON rv.payersId = mvp.payerId 
                    AND rv.receiptMonth = mvp.paymentMonth 
                    AND rv.receiptYear = mvp.paymentYear
                    AND rv.payersServicesId <> mvp.payerServiceId
            GROUP BY rv.receiptId, rv.payersId, rv.payersServicesId, rv.isLinePaid, 
                    mvp.fromPaymentDate,  mvp.toPaymentDate, mvp.paymentMonth, mvp.paymentYear, 
                    mvp.meterLocCode) mrv
                ON rps.fromServiceDate IS NULL
                    AND ifnull(mrv.payersServicesId, rps.payerServiceId) = rps.payerServiceId
                    AND mrv.meterLocCode = rps.serviceLocCode
                    AND (SELECT MAX(strftime(${Constants.DB_FRACT_SEC_TIME}, rsv.startDate)) 
                        FROM ${RatePayerServiceView.VIEW_NAME} rsv 
                        WHERE rsv.payerId = rps.payerId  AND rsv.payerServiceId = rps.payerServiceId
                            AND rsv.serviceLocCode = rps.serviceLocCode AND rsv.isMeterUses = $DB_FALSE
                            AND strftime(${Constants.DB_FRACT_SEC_TIME}, rsv.startDate) <= 
                                strftime(${Constants.DB_FRACT_SEC_TIME}, mrv.toPaymentDate)) = 
                    strftime(${Constants.DB_FRACT_SEC_TIME}, rps.startDate)
    -- payer services with fromServiceDate controls by its receipts
        LEFT JOIN ${ReceiptView.VIEW_NAME} rcv ON rcv.payersId = rps.payerId 
                    AND rcv.receiptMonth = rps.fromServiceMonth AND rcv.receiptYear = rps.fromServiceYear
                    AND rcv.payersServicesId = rps.payerServiceId
        LEFT JOIN ${RatePayerServiceView.VIEW_NAME} psl ON psl.isMeterUses = $DB_FALSE  
            -- Payer services without meters and with fromServiceDate for correct: from... toPaymentDate and debt factor (full months)
                AND psl.fromServiceDate = rps.fromServiceDate
                AND psl.payerId = rps.payerId  AND psl.payerServiceId = rps.payerServiceId
                AND psl.serviceLocCode = rps.serviceLocCode
                AND strftime(${Constants.DB_FRACT_SEC_TIME}, psl.startDate) = 
                    (SELECT MIN(strftime(${Constants.DB_FRACT_SEC_TIME}, startDate)) 
                    FROM ${RatePayerServiceView.VIEW_NAME}
                    WHERE isMeterUses = psl.isMeterUses AND fromServiceDate = psl.fromServiceDate
                        AND payerId = psl.payerId AND payerServiceId = psl.payerServiceId
                        AND serviceLocCode = psl.serviceLocCode
                        AND strftime(${Constants.DB_FRACT_SEC_TIME}, startDate) >= strftime(${Constants.DB_FRACT_SEC_TIME}, rps.fromServiceDate)
                        AND strftime(${Constants.DB_FRACT_SEC_TIME}, startDate) > strftime(${Constants.DB_FRACT_SEC_TIME}, rps.startDate))
    -- rates for services without meters
    WHERE rps.isMeterUses = $DB_FALSE AND ifnull(mrv.isLinePaid, 0) = $DB_FALSE AND ifnull(rcv.isLinePaid, 0) = $DB_FALSE) psd
UNION ALL
SELECT * FROM ${PayerMeterServiceDebtView.VIEW_NAME}    
"""
)
class PayerServiceDebtView(
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
    val fromMeterValue: BigDecimal?,
    val toMeterValue: BigDecimal?,
    val diffMeterValue: BigDecimal?,
    val measureUnit: String?,
    val isMeterUses: Boolean,
    val serviceDebt: BigDecimal,
    val serviceDebtExpr: String?
) {
    companion object {
        const val VIEW_NAME = "payer_service_debts_view"
    }
}