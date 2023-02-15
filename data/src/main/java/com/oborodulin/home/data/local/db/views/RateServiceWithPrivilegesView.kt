package com.oborodulin.home.data.local.db.views

import androidx.room.DatabaseView
import androidx.room.Embedded
import com.oborodulin.home.data.local.db.entities.RateEntity

@DatabaseView(
    viewName = RateServiceWithPrivilegesView.VIEW_NAME,
    value = """
SELECT rsd.* FROM rate_service_last_dates_view rsd JOIN
    (SELECT servicesId, payersServicesId FROM rate_service_last_dates_view
    EXCEPT 
    SELECT servicesId, payersServicesId FROM rate_service_last_dates_view WHERE isPrivileges = 1) rwp
        ON rsd.isPrivileges = 0 AND rsd.servicesId = rwp.servicesId 
            AND IFNULL(rsd.payersServicesId, '') = IFNULL(rwp.payersServicesId, '')
UNION ALL
SELECT rsd.* FROM rate_service_last_dates_view rsd JOIN
    (SELECT servicesId, payersServicesId FROM rate_service_last_dates_view WHERE isPrivileges = 1) rp
        ON rsd.isPrivileges = 1 AND rsd.servicesId = rp.servicesId 
            AND IFNULL(rsd.payersServicesId, '') = IFNULL(rp.payersServicesId, '')
"""
)
class RateServiceWithPrivilegesView(
    @Embedded
    var data: RateEntity
) {
    companion object {
        const val VIEW_NAME = "rate_service_with_privileges_view"
    }
}