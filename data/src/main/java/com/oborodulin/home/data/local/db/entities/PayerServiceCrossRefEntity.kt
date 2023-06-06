package com.oborodulin.home.data.local.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.oborodulin.home.common.data.entities.BaseEntity
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

@Entity(
    tableName = PayerServiceCrossRefEntity.TABLE_NAME,
    indices = [Index(
        value = ["payersId", "servicesId", "isPrivileges"],
        unique = true
    )],
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
    val periodFromDate: OffsetDateTime? = null, // period in year for heating
    val periodToDate: OffsetDateTime? = null,
    val isMeterOwner: Boolean = false,
    val isPrivileges: Boolean = false,
    val isAllocateRate: Boolean = false,
    // warning: servicesId column references a foreign key but it is not part of an index.
    // This may trigger full table scans whenever parent table is modified so you are highly advised to create an index that covers this column.
    @ColumnInfo(index = true) val payersId: UUID,
    @ColumnInfo(index = true) val servicesId: UUID
) : BaseEntity() {

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

    override fun id() = this.payerServiceId

    override fun key(): Int {
        var result = payersId.hashCode()
        result = result * 31 + servicesId.hashCode()
        result = result * 31 + isPrivileges.hashCode()
        return result
    }

    override fun toString(): String {
        val str = StringBuffer()
        str.append("Payer Service Entity")
        fromYear?.let {
            str.append(" from Date: ").append(it).append("-").append(fromMonth).append("-01")
        }
        periodFromDate?.let {
            str.append(" for period ").append(DateTimeFormatter.ISO_LOCAL_DATE.format(it))
                .append(" - ").append(
                    if (periodToDate != null) DateTimeFormatter.ISO_LOCAL_DATE.format(periodToDate) else "..."
                )
        }
        str.append(" [isMeterOwner = ").append(isMeterOwner)
            .append("; isPrivileges = ").append(isPrivileges)
            .append("; isAllocateRate = ").append(isAllocateRate)
            .append("] payerServiceId = ").append(payerServiceId)
        return str.toString()
    }
}