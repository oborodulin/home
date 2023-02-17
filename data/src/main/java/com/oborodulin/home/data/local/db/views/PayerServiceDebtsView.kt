package com.oborodulin.home.data.local.db.views

import androidx.room.DatabaseView
import java.math.BigDecimal
import java.util.*

@DatabaseView(
    viewName = PayerServiceDebtsView.VIEW_NAME,
    value = """
SELECT ptb.payerId, ptb.pos, ptb.name, ptb.rateValue, ptb.fromMeterValue, ptb.toMeterValue, 
        SUM(ptb.diffMeterValue) AS diffMeterValue, ptb.measureUnit,
        SUM(ptb.serviceDebt) AS serviceDebt, ptb.isMeterUses
FROM (SELECT rps.payerId, rps.pos, rps.name, rps.rateValue, rps.fromMeterValue, rps.toMeterValue, NULL diffMeterValue,
        NULL measureUnit,
        (CASE WHEN rps.isPerPerson = 1 THEN rps.rateValue * rps.personsNum ELSE rps.rateValue END) serviceDebt,
        rps.isMeterUses
    FROM rate_payer_services_view rps
    WHERE rps.isMeterUses = 0
    UNION ALL
    SELECT rps.payerId, rps.pos, rps.name, rps.rateValue, rps.fromMeterValue, rps.toMeterValue, mvp.diffMeterValue,
        mvp.measureUnit,
        (CASE WHEN mvp.isDerivedUnit = 0 
            THEN rps.rateValue * mvp.diffMeterValue 
            ELSE CASE rps.type WHEN 'HEATING' THEN rps.rateValue * rps.livingSpace
                    ELSE rps.rateValue
                END
        END) serviceDebt,
        rps.isMeterUses
    FROM rate_payer_services_view rps JOIN meter_value_payments_view mvp 
        ON rps.payerId = mvp.payerId AND rps.payerServiceId = mvp.payerServiceId
            AND 1 = (CASE WHEN mvp.isDerivedUnit = 0 
                        THEN 
                            CASE WHEN mvp.diffMeterValue BETWEEN IFNULL(rps.fromMeterValue, mvp.diffMeterValue) 
                                                            AND IFNULL(rps.toMeterValue, mvp.diffMeterValue)
                                THEN 1
                                ELSE 0
                            END
                        ELSE 1
                    END) 
    WHERE rps.isMeterUses = 1) ptb
GROUP BY ptb.payerId, ptb.pos, ptb.name, ptb.rateValue, ptb.fromMeterValue, ptb.toMeterValue, ptb.measureUnit, 
        ptb.isMeterUses
ORDER BY ptb.payerId, ptb.pos        
"""
)
class PayerServiceDebtsView(
    val payerId: UUID,
    val pos: Int,
    val name: String,
    val rateValue: BigDecimal,
    val fromMeterValue: BigDecimal?,
    val toMeterValue: BigDecimal?,
    val diffMeterValue: BigDecimal?,
    val measureUnit: String?,
    val serviceDebt: BigDecimal,
    val isMeterUses: Boolean
) {
    companion object {
        const val VIEW_NAME = "payer_service_debts_view"
    }
}