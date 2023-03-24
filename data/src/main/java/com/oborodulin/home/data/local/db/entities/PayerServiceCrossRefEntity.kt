package com.oborodulin.home.data.local.db.entities

import androidx.room.*
import java.time.OffsetDateTime
import java.util.*

@Entity(
    tableName = PayerServiceCrossRefEntity.TABLE_NAME,
    indices = [Index(value = ["payersId", "servicesId", "isPrivileges"], unique = true)],
    foreignKeys = [ForeignKey(
        entity = PayerEntity::class,
        parentColumns = arrayOf("payerId"),
        childColumns = arrayOf("payersId"),
        onDelete = ForeignKey.CASCADE,
        deferred = true
    ), ForeignKey(
        entity = ServiceEntity::class,
        parentColumns = arrayOf("serviceId"),
        childColumns = arrayOf("servicesId"),
        onDelete = ForeignKey.CASCADE,
        deferred = true
    )]
)
data class PayerServiceCrossRefEntity(
    @PrimaryKey val payerServiceId: UUID = UUID.randomUUID(),
    val fromMonth: Int? = null, // if null then from meters data values
    val fromYear: Int? = null,
    val isMeterOwner: Boolean = false,
    val isPrivileges: Boolean = false,
    val isAllocateRate: Boolean = false,
    // warning: servicesId column references a foreign key but it is not part of an index.
    // This may trigger full table scans whenever parent table is modified so you are highly advised to create an index that covers this column.
    @ColumnInfo(index = true) val payersId: UUID,
    @ColumnInfo(index = true) val servicesId: UUID
) {
    companion object {
        const val TABLE_NAME = "payers_services"

        fun defaultPayerService(
            payerId: UUID, serviceId: UUID,
            fromMonth: Int? = OffsetDateTime.now().monthValue,
            fromYear: Int? = OffsetDateTime.now().year,
            isMeterOwner: Boolean = false, isPrivileges: Boolean = false,
            isAllocateRate: Boolean = false
        ) = PayerServiceCrossRefEntity(
            payersId = payerId, servicesId = serviceId, fromMonth = fromMonth, fromYear = fromYear,
            isMeterOwner = isMeterOwner, isPrivileges = isPrivileges,
            isAllocateRate = isAllocateRate
        )

        fun privilegesPayerService(
            payerId: UUID, serviceId: UUID,
            fromMonth: Int? = null, fromYear: Int? = null,
            isMeterOwner: Boolean = false, isAllocateRate: Boolean = false
        ) = defaultPayerService(
            payerId = payerId, serviceId = serviceId, fromMonth = fromMonth, fromYear = fromYear,
            isMeterOwner = isMeterOwner, isPrivileges = true, isAllocateRate = isAllocateRate
        )

        fun allocateRatePayerService(
            payerId: UUID, serviceId: UUID,
            fromMonth: Int? = null, fromYear: Int? = null,
            isMeterOwner: Boolean = false, isPrivileges: Boolean = false
        ) = defaultPayerService(
            payerId = payerId, serviceId = serviceId, fromMonth = fromMonth, fromYear = fromYear,
            isMeterOwner = isMeterOwner, isPrivileges = isPrivileges, isAllocateRate = true
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PayerServiceCrossRefEntity
        if (payerServiceId != other.payerServiceId) return false

        return true
    }

    override fun hashCode(): Int {
        return payerServiceId.hashCode()
    }
}