package com.oborodulin.home.data.local.db.views

import androidx.room.DatabaseView
import androidx.room.Embedded
import androidx.room.TypeConverters
import com.oborodulin.home.data.local.db.converters.DateTypeConverter
import com.oborodulin.home.data.local.db.entities.PayerServiceCrossRefEntity
import com.oborodulin.home.data.util.Constants
import java.time.OffsetDateTime
import java.util.*

@DatabaseView(
    viewName = PayerServiceView.VIEW_NAME,
    value = """
SELECT psv.*,
        strftime(${Constants.DB_FRACT_SEC_TIME}, datetime(psv.fromDate, 'localtime')) || 
            printf('%+.2d:%.2d', round((julianday(psv.fromDate, 'localtime') - julianday(psv.fromDate)) * 24), 
                abs(round((julianday(psv.fromDate, 'localtime') - julianday(psv.fromDate)) * 24 * 60) % 60)) AS fromServiceDate 
FROM (SELECT sv.*, ps.payerServiceId, ps.payersId, ps.fromMonth, ps.fromYear, 
        strftime(${Constants.DB_FRACT_SEC_TIME}, printf('%d-%02d-01T00:00:00.000', ps.fromYear, ps.fromMonth)) AS fromDate,
        ps.isMeterOwner, ps.isPrivileges, ps.isAllocateRate 
    FROM ${ServiceView.VIEW_NAME} sv JOIN ${PayerServiceCrossRefEntity.TABLE_NAME} ps ON ps.servicesId = sv.serviceId) psv
ORDER BY psv.servicePos
"""
)
class PayerServiceView(
    @Embedded
    val service: ServiceView,
    val payerServiceId: UUID,
    val payersId: UUID,
    val fromMonth: Int? = null,
    val fromYear: Int? = null,
    @field:TypeConverters(DateTypeConverter::class)
    val fromDate: OffsetDateTime? = null,
    val isMeterOwner: Boolean,
    val isPrivileges: Boolean,
    val isAllocateRate: Boolean,
    val fromServiceDate: OffsetDateTime? = null
) {
    companion object {
        const val VIEW_NAME = "payer_services_view"
    }
}