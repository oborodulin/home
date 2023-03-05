package com.oborodulin.home.data.local.db.views

import androidx.room.DatabaseView
import com.oborodulin.home.data.local.db.entities.PayerEntity
import com.oborodulin.home.data.local.db.entities.RateEntity
import com.oborodulin.home.data.util.ServiceType
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*

@DatabaseView(
    viewName = RatePayerServiceView.VIEW_NAME,
    value = """
SELECT rps.*, (CASE WHEN EXISTS(SELECT psm.payerServiceMeterId FROM payers_services_meters psm WHERE psm.payersServicesId = rps.payerServiceId) 
                    THEN 1 
                    ELSE 0 
                END) isMeterUses
FROM (SELECT p.payerId, p.personsNum, p.totalArea, p.livingSpace, p.heatedVolume, psv.payerServiceId, psv.isAllocateRate, 
        psv.serviceId, psv.serviceName, psv.servicePos, psv.serviceType, psv.localeCode AS serviceLocaleCode, 
        r.startDate, r.fromMeterValue, r.toMeterValue, r.rateValue, r.isPerPerson, r.isPrivileges
    FROM ${PayerEntity.TABLE_NAME} p JOIN ${PayerServiceView.VIEW_NAME} psv ON psv.payersId = p.payerId 
                                        AND NOT EXISTS(SELECT rateId FROM ${RateEntity.TABLE_NAME} WHERE payersServicesId = psv.payerServiceId)
        JOIN (SELECT * FROM ${RateEntity.TABLE_NAME} WHERE payersServicesId IS NULL) r ON r.servicesId = psv.servicesId 
                                                                    AND r.isPrivileges = psv.isPrivileges
    UNION ALL
    SELECT p.payerId, p.personsNum, p.totalArea, p.livingSpace, p.heatedVolume, psv.payerServiceId, psv.isAllocateRate, 
        psv.serviceId, psv.serviceName, psv.servicePos, psv.serviceType, psv.localeCode AS serviceLocaleCode, 
        r.startDate, r.fromMeterValue, r.toMeterValue, r.rateValue, r.isPerPerson, r.isPrivileges
    FROM ${PayerEntity.TABLE_NAME} p JOIN ${PayerServiceView.VIEW_NAME} psv ON psv.payersId = p.payerId
        JOIN ${RateEntity.TABLE_NAME} r ON r.payersServicesId = psv.payerServiceId AND r.isPrivileges = psv.isPrivileges) rps
"""
)
class RatePayerServiceView(
    val payerId: UUID,
    val personsNum: Int,
    val totalArea: BigDecimal?,
    val livingSpace: BigDecimal?,
    val heatedVolume: BigDecimal?,
    val payerServiceId: UUID,
    val isAllocateRate: Boolean,
    val serviceId: UUID,
    val serviceName: String,
    val servicePos: Int,
    val serviceType: ServiceType,
    val serviceLocaleCode: String,
    val startDate: OffsetDateTime,
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