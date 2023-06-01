package com.oborodulin.home.data.local.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.oborodulin.home.common.data.entities.BaseEntity
import java.time.OffsetDateTime
import java.util.UUID

@Entity(
    tableName = ServiceActivityEntity.TABLE_NAME,
    //indices = [Index(value = ["payersServicesId", "fromDate", "isActive"], unique = true)],
    foreignKeys = [ForeignKey(
        entity = PayerServiceCrossRefEntity::class,
        parentColumns = arrayOf("payerServiceId"),
        childColumns = arrayOf("payersServicesId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class ServiceActivityEntity(
    @PrimaryKey val serviceActivityId: UUID = UUID.randomUUID(),
    val fromDate: OffsetDateTime,
    val toDate: OffsetDateTime? = null,
    val isActive: Boolean = true,
    @ColumnInfo(index = true) val payersServicesId: UUID
) : BaseEntity() {

    companion object {
        const val TABLE_NAME = "service_activities"

        fun populateServiceActivity(
            payerServiceId: UUID, fromDate: OffsetDateTime, toDate: OffsetDateTime? = null,
            isActive: Boolean = true
        ) =
            ServiceActivityEntity(
                payersServicesId = payerServiceId,
                fromDate = fromDate,
                toDate = toDate,
                isActive = isActive
            )
    }

    override fun id() = this.serviceActivityId
}