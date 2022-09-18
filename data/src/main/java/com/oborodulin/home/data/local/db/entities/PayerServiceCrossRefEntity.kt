package com.oborodulin.home.data.local.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import java.util.*

@Entity(
    tableName = PayerServiceCrossRefEntity.TABLE_NAME,
    primaryKeys = ["payersId", "servicesId"],
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
class PayerServiceCrossRefEntity(
    val payersId: UUID,
    val servicesId: UUID,
    var isAllocateRate: Boolean = false,
) : BaseEntity() {
    companion object {
        const val TABLE_NAME = "payer_services"
    }
}