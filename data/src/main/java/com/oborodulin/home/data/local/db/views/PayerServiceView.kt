package com.oborodulin.home.data.local.db.views

import androidx.room.DatabaseView
import androidx.room.Embedded
import com.oborodulin.home.data.local.db.entities.PayerServiceCrossRefEntity
import java.util.*

@DatabaseView(
    viewName = PayerServiceView.VIEW_NAME,
    value = """
SELECT sv.*, ps.payerServiceId, ps.payersId, ps.isMeterOwner, ps.isPrivileges, ps.isAllocateRate 
FROM ${ServiceView.VIEW_NAME} sv JOIN ${PayerServiceCrossRefEntity.TABLE_NAME} ps ON ps.servicesId = sv.serviceId
ORDER BY sv.servicePos
"""
)
class PayerServiceView(
    @Embedded
    val service: ServiceView,
    val payerServiceId: UUID,
    val payersId: UUID,
    val isMeterOwner: Boolean,
    val isPrivileges: Boolean,
    val isAllocateRate: Boolean
) {
    companion object {
        const val VIEW_NAME = "payer_services_view"
    }
}