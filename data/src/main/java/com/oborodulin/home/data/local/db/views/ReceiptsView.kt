package com.oborodulin.home.data.local.db.views

import androidx.room.DatabaseView
import androidx.room.Embedded
import com.oborodulin.home.data.local.db.entities.ReceiptEntity
import com.oborodulin.home.data.local.db.entities.ReceiptLineEntity

@DatabaseView(
    viewName = ReceiptsView.VIEW_NAME,
    value = """
SELECT r.*, rl.* FROM receipts AS r JOIN receipt_lines AS rl ON rl.receiptsId = r.receiptId
"""
)
class ReceiptsView(
    @Embedded
    val receipt: ReceiptEntity,
    @Embedded
    val line: ReceiptLineEntity,
) {
    companion object {
        const val VIEW_NAME = "receipts_view"
    }
}