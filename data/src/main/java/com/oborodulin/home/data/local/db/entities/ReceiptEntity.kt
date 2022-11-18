package com.oborodulin.home.data.local.db.entities

import androidx.room.*
import java.time.Month
import java.time.Year
import java.util.*

@Entity(
    tableName = ReceiptEntity.TABLE_NAME,
    indices = [Index(value = ["payersId", "receiptYear", "receiptMonth"], unique = true)],
    foreignKeys = [ForeignKey(
        entity = PayerEntity::class,
        parentColumns = arrayOf("payerId"),
        childColumns = arrayOf("payersId"),
        onDelete = ForeignKey.CASCADE
    )]
)
class ReceiptEntity(
    @PrimaryKey var receiptId: UUID = UUID.randomUUID(),
    var receiptMonth: Int,
    var receiptYear: Int,
    var isPaid: Boolean = false,
    @ColumnInfo(index = true) var payersId: UUID,
) {
    companion object {
        const val TABLE_NAME = "receipts"
    }
}
