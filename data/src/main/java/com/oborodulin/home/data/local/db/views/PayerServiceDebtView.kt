package com.oborodulin.home.data.local.db.views

import androidx.room.DatabaseView
import com.oborodulin.home.common.util.Constants.CONV_COEFF_BIGDECIMAL
import com.oborodulin.home.data.util.Constants
import com.oborodulin.home.data.util.ServiceType
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*

@DatabaseView(
    viewName = PayerServiceDebtView.VIEW_NAME,
    value = """
SELECT rps.payerId, rps.personsNum, rps.totalArea, rps.livingSpace, mrv.paymentDate, 
        mrv.paymentMonth, mrv.paymentYear, rps.isPerPerson, rps.pos, rps.type, rps.name, 
        rps.serviceLocaleCode, rps.rateValue, rps.fromMeterValue, rps.toMeterValue, 
        NULL AS diffMeterValue, NULL AS measureUnit, 0 AS isDerivedUnit, rps.serviceId, 
        rps.payerServiceId,
        (CASE WHEN rps.isPerPerson = 1
            -- GAS, GARBAGE
            THEN rps.rateValue * rps.personsNum
            ELSE CASE WHEN rps.type IN (${Constants.SRV_RENT_VAL}) THEN rps.rateValue * ifnull(rps.totalArea, 1)
                    WHEN rps.type IN (${Constants.SRV_HEATING_VAL}) THEN rps.rateValue * ifnull(rps.livingSpace, 1)
                    -- DOORPHONE, PHONE, INTERNET, USGO
                    ELSE rps.rateValue
                END
        END) serviceDebt,
        rps.isMeterUses
FROM ${RatePayerServiceView.VIEW_NAME} rps JOIN 
        (SELECT rv.receiptId, rv.payersId, rv.payersServicesId, rv.isLinePaid, 
                mvp.paymentDate, mvp.paymentMonth, mvp.paymentYear, mvp.localeCode
            FROM ${MeterValuePaymentView.VIEW_NAME} mvp LEFT JOIN receipts_view rv 
                ON rv.payersId = mvp.payerId 
                AND rv.receiptMonth = mvp.paymentMonth 
                AND rv.receiptYear = mvp.paymentYear
                AND rv.payersServicesId <> mvp.payerServiceId
        GROUP BY rv.receiptId, rv.payersId, rv.payersServicesId, rv.isLinePaid, 
                mvp.paymentDate, mvp.paymentMonth, mvp.paymentYear, mvp.localeCode) mrv
            ON ifnull(mrv.payersServicesId, rps.payerServiceId) = rps.payerServiceId
                AND mrv.localeCode = rps.serviceLocaleCode
WHERE rps.isMeterUses = 0
    AND strftime(${Constants.DB_FRACT_SEC_TIME}, rps.startDate) = ifnull(
                                                    (SELECT MAX(strftime(${Constants.DB_FRACT_SEC_TIME}, rsv.startDate)) 
                                                    FROM ${RatePayerServiceView.VIEW_NAME} rsv 
                                                    WHERE rsv.payerId = rps.payerId 
                                                        AND rsv.payerServiceId = rps.payerServiceId
                                                        AND rsv.serviceLocaleCode = rps.serviceLocaleCode
                                                        AND rsv.isMeterUses = 0
                                                        AND rsv.startDate <= mrv.paymentDate),
                                                    strftime(${Constants.DB_FRACT_SEC_TIME}, rps.startDate))
    AND ifnull(mrv.isLinePaid, 0) = 0
UNION ALL
SELECT rps.payerId, rps.personsNum, rps.totalArea, rps.livingSpace, mvp.paymentDate, 
        mvp.paymentMonth, mvp.paymentYear, 0 AS isPerPerson, rps.pos, rps.type, rps.name, 
        rps.serviceLocaleCode, rps.rateValue, rps.fromMeterValue, rps.toMeterValue, 
        mvp.diffMeterValue, mvp.measureUnit, mvp.isDerivedUnit, rps.serviceId, rps.payerServiceId,
        (CASE WHEN mvp.isDerivedUnit = 0 
            THEN rps.rateValue * mvp.diffMeterValue / ${CONV_COEFF_BIGDECIMAL}.0
            ELSE CASE rps.type WHEN ${Constants.SRV_HEATING_VAL} THEN rps.rateValue * rps.livingSpace
                    ELSE rps.rateValue
                END
        END) serviceDebt,
        rps.isMeterUses
FROM ${RatePayerServiceView.VIEW_NAME} rps JOIN ${MeterValuePaymentView.VIEW_NAME} mvp 
    ON mvp.payerId = rps.payerId AND mvp.payerServiceId = rps.payerServiceId
        AND mvp.localeCode = rps.serviceLocaleCode
        AND strftime(${Constants.DB_FRACT_SEC_TIME}, rps.startDate) = ifnull(
                                                    (SELECT MAX(strftime(${Constants.DB_FRACT_SEC_TIME}, rsv.startDate)) 
                                                    FROM ${RatePayerServiceView.VIEW_NAME} rsv 
                                                    WHERE rsv.payerId = rps.payerId 
                                                        AND rsv.payerServiceId = rps.payerServiceId
                                                        AND rsv.serviceLocaleCode = rps.serviceLocaleCode
                                                        AND rsv.isMeterUses = 1 
                                                        AND rsv.startDate <= mvp.paymentDate),
                                                    strftime(${Constants.DB_FRACT_SEC_TIME}, rps.startDate))
        AND 1 = (CASE WHEN mvp.isDerivedUnit = 0 
                        THEN CASE WHEN mvp.diffMeterValue BETWEEN IFNULL(rps.fromMeterValue, mvp.diffMeterValue) 
                                                            AND IFNULL(rps.toMeterValue, mvp.diffMeterValue)
                                THEN 1
                                ELSE 0
                            END
                        ELSE 1
                    END)
    LEFT JOIN ${ReceiptView.VIEW_NAME} rv ON rv.receiptMonth = mvp.paymentMonth AND rv.receiptYear = mvp.paymentYear
                                AND rv.meterValuesId = mvp.meterValueId
WHERE rps.isMeterUses = 1 AND ifnull(rv.isLinePaid, 0) = 0
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
    val pos: Int,
    val type: ServiceType,
    val name: String,
    val serviceLocaleCode: String,
    val rateValue: BigDecimal,
    val fromMeterValue: BigDecimal?,
    val toMeterValue: BigDecimal?,
    val diffMeterValue: BigDecimal?,
    val measureUnit: String?,
    val isDerivedUnit: Boolean,
    val serviceId: UUID,
    val payerServiceId: UUID,
    val serviceDebt: BigDecimal,
    val isMeterUses: Boolean
) {
    companion object {
        const val VIEW_NAME = "payer_service_debts_view"
    }
}