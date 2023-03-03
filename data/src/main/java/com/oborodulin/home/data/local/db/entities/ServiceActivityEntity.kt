package com.oborodulin.home.data.local.db.entities

import androidx.room.*
import java.time.OffsetDateTime
import java.util.*

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
class ServiceActivityEntity(
    @PrimaryKey val serviceActivityId: UUID = UUID.randomUUID(),
    val fromDate: OffsetDateTime,
    val toDate: OffsetDateTime? = null,
    val isActive: Boolean = true,
    @ColumnInfo(index = true) val payersServicesId: UUID
) {
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ServiceActivityEntity
        if (serviceActivityId != other.serviceActivityId) return false

        return true
    }

    override fun hashCode(): Int {
        return serviceActivityId.hashCode()
    }
}