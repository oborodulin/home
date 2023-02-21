package com.oborodulin.home.data.local.db.views

import androidx.room.DatabaseView
import com.oborodulin.home.data.util.ServiceType
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*

@DatabaseView(
    viewName = PayerServiceDebtsView.VIEW_NAME,
    value = """
SELECT rps.payerId, rps.personsNum, rps.totalArea, rps.livingSpace, mrv.paymentDate, 
        mrv.paymentMonth, mrv.paymentYear, rps.isPerPerson, rps.pos, rps.type, rps.name, 
        rps.rateValue, rps.fromMeterValue, rps.toMeterValue, NULL AS diffMeterValue, NULL AS measureUnit, 
        0 AS isDerivedUnit, rps.payerServiceId,
        (CASE WHEN rps.isPerPerson = 1
            -- GAS, GARBAGE
            THEN rps.rateValue * rps.personsNum
            ELSE CASE WHEN rps.type IN ('RENT') THEN rps.rateValue * ifnull(rps.totalArea, 1)
                    WHEN rps.type IN ('HEATING') THEN rps.rateValue * ifnull(rps.livingSpace, 1)
                    -- DOORPHONE, PHONE, INTERNET, USGO
                    ELSE rps.rateValue
                END
        END) serviceDebt,
        rps.isMeterUses
FROM rate_payer_services_view rps JOIN 
        (SELECT rv.receiptId, rv.payersId, rv.payersServicesId, rv.isLinePaid, 
                mvp.paymentDate, mvp.paymentMonth, mvp.paymentYear
            FROM meter_value_payments_view mvp LEFT JOIN receipts_view rv 
                ON rv.payersId = mvp.payerId 
                AND rv.receiptMonth = mvp.paymentMonth 
                AND rv.receiptYear = mvp.paymentYear
                AND rv.payersServicesId <> mvp.payerServiceId
        GROUP BY rv.receiptId, rv.payersId, rv.payersServicesId, rv.isLinePaid, 
                mvp.paymentDate, mvp.paymentMonth, mvp.paymentYear) mrv
            ON ifnull(mrv.payersServicesId, rps.payerServiceId) = rps.payerServiceId
WHERE rps.isMeterUses = 0
    AND strftime('%Y-%m-%d %H:%M:%f', rps.startDate) = ifnull(
                                                    (SELECT MAX(strftime('%Y-%m-%d %H:%M:%f', rv.startDate)) 
                                                    FROM rate_payer_services_view rv 
                                                    WHERE rv.payerId = rps.payerId 
                                                        AND rv.payerServiceId = rps.payerServiceId
                                                        AND rv.isMeterUses = 0
                                                        AND rv.startDate <= mrv.paymentDate),
                                                    strftime('%Y-%m-%d %H:%M:%f', rps.startDate))
    AND ifnull(mrv.isLinePaid, 0) = 0
UNION ALL
SELECT rps.payerId, rps.personsNum, rps.totalArea, rps.livingSpace, mvp.paymentDate, 
        mvp.paymentMonth, mvp.paymentYear, 0 AS isPerPerson, rps.pos, rps.type, rps.name, 
        rps.rateValue, rps.fromMeterValue, rps.toMeterValue, mvp.diffMeterValue, mvp.measureUnit, 
        mvp.isDerivedUnit, rps.payerServiceId,
        (CASE WHEN mvp.isDerivedUnit = 0 
            THEN rps.rateValue * mvp.diffMeterValue 
            ELSE CASE rps.type WHEN 'HEATING' THEN rps.rateValue * rps.livingSpace
                    ELSE rps.rateValue
                END
        END) serviceDebt,
        rps.isMeterUses
FROM rate_payer_services_view rps JOIN meter_value_payments_view mvp 
    ON mvp.payerId = rps.payerId AND mvp.payerServiceId = rps.payerServiceId
        AND strftime('%Y-%m-%d %H:%M:%f', rps.startDate) = ifnull(
                                                    (SELECT MAX(strftime('%Y-%m-%d %H:%M:%f', rv.startDate)) 
                                                    FROM rate_payer_services_view rv 
                                                    WHERE rv.payerId = rps.payerId 
                                                        AND rv.payerServiceId = rps.payerServiceId
                                                        AND rv.isMeterUses = 1 
                                                        AND rv.startDate <= mvp.paymentDate),
                                                    strftime('%Y-%m-%d %H:%M:%f', rps.startDate))
        AND 1 = (CASE WHEN mvp.isDerivedUnit = 0 
                        THEN CASE WHEN mvp.diffMeterValue BETWEEN IFNULL(rps.fromMeterValue, mvp.diffMeterValue) 
                                                            AND IFNULL(rps.toMeterValue, mvp.diffMeterValue)
                                THEN 1
                                ELSE 0
                            END
                        ELSE 1
                    END)
    LEFT JOIN receipts_view rv ON rv.receiptMonth = mvp.paymentMonth AND rv.receiptYear = mvp.paymentYear
                                AND rv.meterValuesId = mvp.meterValueId
WHERE rps.isMeterUses = 1 AND ifnull(rv.isLinePaid, 0) = 0
"""
)
class PayerServiceDebtsView(
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
    val rateValue: BigDecimal,
    val fromMeterValue: BigDecimal?,
    val toMeterValue: BigDecimal?,
    val diffMeterValue: BigDecimal?,
    val measureUnit: String?,
    val isDerivedUnit: Boolean,
    val payerServiceId: UUID,
    val serviceDebt: BigDecimal,
    val isMeterUses: Boolean
) {
    companion object {
        const val VIEW_NAME = "payer_service_debts_view"
    }
}