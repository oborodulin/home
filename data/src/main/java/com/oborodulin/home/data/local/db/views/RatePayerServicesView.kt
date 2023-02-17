package com.oborodulin.home.data.local.db.views

import androidx.room.DatabaseView
import com.oborodulin.home.data.util.ServiceType
import java.math.BigDecimal
import java.util.*

@DatabaseView(
    viewName = RatePayerServicesView.VIEW_NAME,
    value = """
SELECT rps.*, (CASE WHEN EXISTS(SELECT psm.payerServiceMeterId FROM payers_services_meters psm WHERE psm.payersServicesId = rps.payerServiceId) 
                    THEN 1 
                    ELSE 0 
                END) isMeterUses
FROM (SELECT p.payerId, p.personsNum, p.totalArea, p.livingSpace, p.heatedVolume, ps.payerServiceId, ps.isAllocateRate, 
        sv.serviceId, sv.name, sv.pos, sv.type, rsl.fromMeterValue, rsl.toMeterValue, rsl.rateValue, rsl.isPerPerson, rsl.isPrivileges
    FROM payers p JOIN payers_services ps ON ps.payersId = p.payerId 
                                        AND NOT EXISTS(SELECT rateId FROM rate_service_last_dates_view WHERE payersServicesId = ps.payerServiceId)
        JOIN (SELECT * FROM rate_service_last_dates_view WHERE payersServicesId IS NULL) rsl ON rsl.servicesId = ps.servicesId 
                                                                                            AND rsl.isPrivileges = ps.isPrivileges
        JOIN services_view AS sv ON sv.serviceId = ps.servicesId
    UNION ALL
    SELECT p.payerId, p.personsNum, p.totalArea, p.livingSpace, p.heatedVolume, ps.payerServiceId, ps.isAllocateRate, 
        sv.serviceId, sv.name, sv.pos, sv.type, rsl.fromMeterValue, rsl.toMeterValue, rsl.rateValue, rsl.isPerPerson, rsl.isPrivileges
    FROM payers p JOIN payers_services ps ON ps.payersId = p.payerId
        JOIN rate_service_last_dates_view rsl ON rsl.payersServicesId = ps.payerServiceId AND rsl.isPrivileges = ps.isPrivileges
        JOIN services_view AS sv ON sv.serviceId = ps.servicesId) rps
"""
)
class RatePayerServicesView(
    val payerId: UUID,
    val personsNum: Int,
    val totalArea: BigDecimal?,
    val livingSpace: BigDecimal?,
    val heatedVolume: BigDecimal?,
    val payerServiceId: UUID,
    val isAllocateRate: Boolean,
    val serviceId: UUID,
    val name: String,
    val pos: Int,
    val type: ServiceType,
    val fromMeterValue: BigDecimal?,
    val toMeterValue: BigDecimal?,
    val rateValue: BigDecimal,
    val isPerPerson: Boolean,
    val isPrivileges: Boolean,
    val isMeterUses: Boolean
) {
    companion object {
        const val VIEW_NAME = "rate_payer_services_view"
    }
}