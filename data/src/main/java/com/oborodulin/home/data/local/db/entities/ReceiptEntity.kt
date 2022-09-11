package com.oborodulin.home.data.local.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import java.time.Month
import java.time.Year
import java.util.*

@Entity(
    tableName = ReceiptEntity.TABLE_NAME,
    indices = [Index(value = ["payersId", "receiptYear", "receiptMonth"], unique = true)],
    foreignKeys = [ForeignKey(
        entity = PayerEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("payersId"),
        onDelete = ForeignKey.CASCADE
    )]
)
class ReceiptEntity(
    var receiptMonth: Month,
    var receiptYear: Year,
    var isPaid: Boolean = false,
    @ColumnInfo(index = true) var payersId: UUID,
) : BaseEntity() {
    companion object {
        const val TABLE_NAME = "receipts"
    }
}
