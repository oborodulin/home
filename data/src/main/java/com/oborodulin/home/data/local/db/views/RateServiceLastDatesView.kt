package com.oborodulin.home.data.local.db.views

import androidx.room.DatabaseView
import androidx.room.Embedded
import com.oborodulin.home.data.local.db.entities.RateEntity

@DatabaseView(
    viewName = RateServiceLastDatesView.VIEW_NAME,
    value = """
SELECT r.* FROM rates r JOIN
    (SELECT servicesId, payersServicesId, MAX(strftime('%Y-%m-%d %H:%M:%f', startDate)) maxStartDate
        FROM rates GROUP BY servicesId, payersServicesId) rmd
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