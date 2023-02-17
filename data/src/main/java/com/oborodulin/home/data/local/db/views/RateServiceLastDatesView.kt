package com.oborodulin.home.data.local.db.views

import androidx.room.DatabaseView
import androidx.room.Embedded
import com.oborodulin.home.data.local.db.entities.RateEntity

@DatabaseView(
    viewName = RateServiceLastDatesView.VIEW_NAME,
    value = """
SELECT r.* FROM rates r JOIN
    (SELECT rp.servicesId, rp.payersServicesId, MAX(strftime('%Y-%m-%d %H:%M:%f', rp.startDate)) maxStartDate
        FROM    (SELECT r.servicesId, r.payersServicesId, r.fromMeterValue, r.toMeterValue, r.startDate 
                    FROM rates r WHERE r.payersServicesId IS NULL
                UNION ALL
                SELECT r.servicesId, r.payersServicesId, r.fromMeterValue, r.toMeterValue, r.startDate 
                    FROM rates r JOIN payers_services ps ON r.payersServicesId = ps.payerServiceId 
                                                        AND r.isPrivileges = ps.isPrivileges) rp
    GROUP BY rp.servicesId, rp.payersServicesId, rp.fromMeterValue, rp.toMeterValue) rmd
    ON r.servicesId = rmd.servicesId AND IFNULL(r.payersServicesId, '') = IFNULL(rmd.payersServicesId, '')
        AND strftime('%Y-%m-%d %H:%M:%f', r.startDate) = rmd.maxStartDate
"""
)
class RateServiceLastDatesView(
    @Embedded
    var data: RateEntity
) {
    companion object {
        const val VIEW_NAME = "rate_service_last_dates_view"
    }
}