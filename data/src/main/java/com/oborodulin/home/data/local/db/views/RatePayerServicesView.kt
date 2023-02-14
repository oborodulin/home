package com.oborodulin.home.data.local.db.views

import androidx.room.DatabaseView
import java.math.BigDecimal
import java.util.*

@DatabaseView(
    viewName = RatePayerServicesView.VIEW_NAME,
    value = """
SELECT p.payerId, p.personsNum, p.totalArea, p.livingSpace, p.heatedVolume, ps.payerServiceId, ps.isAllocateRate, 
    sv.serviceId, sv.name, sv.pos, r.fromMeterValue, r.toMeterValue, r.rateValue, r.isPerPerson, r.isPrivileges
FROM payers p JOIN payers_services ps ON ps.payersId = p.payerId 
    JOIN services_view AS sv ON sv.serviceId = ps.servicesId
    JOIN (SELECT rsd.* FROM rate_service_with_privileges_view rsd JOIN
            (SELECT rsp.servicesId
                FROM rate_service_with_privileges_view rsp 
                    JOIN (SELECT servicesId FROM payers_services GROUP BY servicesId) ps ON rsp.servicesId = ps.servicesId 
                                                                                        AND rsp.payersServicesId IS NULL
            EXCEPT
            SELECT rsp.servicesId
            FROM rate_service_with_privileges_view rsp JOIN payers_services ps ON rsp.payersServicesId = ps.payerServiceId) rs
                ON rsd.payersServicesId IS NULL AND rsd.servicesId = rs.servicesId) r
        ON r.servicesId = ps.servicesId
UNION ALL
-- rate for payers
SELECT p.payerId, p.personsNum, p.totalArea, p.livingSpace, p.heatedVolume, ps.payerServiceId, ps.isAllocateRate, 
    sv.serviceId, sv.name, sv.pos, r.fromMeterValue, r.toMeterValue, r.rateValue, r.isPerPerson, r.isPrivileges
FROM payers p JOIN payers_services ps ON ps.payersId = p.payerId 
    JOIN services_view AS sv ON sv.serviceId = ps.servicesId
    JOIN (SELECT rsd.* FROM rate_service_with_privileges_view rsd JOIN
            (SELECT rsp.payersServicesId FROM rate_service_with_privileges_view rsp JOIN payers_services ps 
                                                ON rsp.payersServicesId = ps.payerServiceId) rp
                ON rsd.payersServicesId = rp.payersServicesId) r
        ON r.payersServicesId = ps.payerServiceId
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
    val fromMeterValue: BigDecimal?,
    val toMeterValue: BigDecimal?,
    val rateValue: BigDecimal,
    val isPerPerson: Boolean,
    val isPrivileges: Boolean,
) {
    companion object {
        const val VIEW_NAME = "rate_payer_services_view"
    }
}