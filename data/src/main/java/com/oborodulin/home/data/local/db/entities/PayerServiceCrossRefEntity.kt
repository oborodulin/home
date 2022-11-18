package com.oborodulin.home.data.local.db.entities

import androidx.room.*
import java.util.*

@Entity(
    tableName = PayerServiceCrossRefEntity.TABLE_NAME,
    indices = [Index(value = ["payersId", "servicesId"], unique = true)],
    foreignKeys = [ForeignKey(
        entity = PayerEntity::class,
        parentColumns = arrayOf("payerId"),
        childColumns = arrayOf("payersId"),
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = ServiceEntity::class,
        parentColumns = arrayOf("serviceId"),
        childColumns = arrayOf("servicesId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class PayerServiceCrossRefEntity(
    @PrimaryKey var payerServiceId: UUID = UUID.randomUUID(),
    //warning: servicesId column references a foreign key but it is not part of an index.
    // This may trigger full table scans whenever parent table is modified so you are highly advised to create an index that covers this column.
    @ColumnInfo(index = true) val payersId: UUID,
    @ColumnInfo(index = true) val servicesId: UUID,
    var isAllocateRate: Boolean = false,
) {
    companion object {
        const val TABLE_NAME = "payers_services"
    }
}