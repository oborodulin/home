package com.oborodulin.home.data.local.db.entities

import androidx.room.Embedded
import androidx.room.Relation

data class ReceiptWithLines(
    @Embedded
    val receipt: ReceiptEntity,
    @Relation(parentColumn = "receiptId", entityColumn = "receiptsId")
    val lines: List<ReceiptLineEntity> = emptyList()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ReceiptWithLines
        if (receipt.receiptId != other.receipt.receiptId) return false

        return true
    }

    override fun hashCode(): Int {
        return receipt.receiptId.hashCode()
    }
}
