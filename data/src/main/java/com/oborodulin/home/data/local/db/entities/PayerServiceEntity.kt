package com.oborodulin.home.data.local.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import java.util.*

@Entity(
    tableName = PayerServiceEntity.TABLE_NAME,
    indices = [Index(value = ["payersId", "servicesId"], unique = true)],
    foreignKeys = [ForeignKey(
        entity = PayerEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("payersId"),
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = ServiceEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("servicesId"),
        onDelete = ForeignKey.CASCADE
    )]
)
class PayerServiceEntity(
    @ColumnInfo(index = true) var payersId: UUID,
    @ColumnInfo(index = true) var servicesId: UUID,
    var isAllocateRate: Boolean = false,
) : BaseEntity() {
    companion object {
        const val TABLE_NAME = "payer_services"
    }
}