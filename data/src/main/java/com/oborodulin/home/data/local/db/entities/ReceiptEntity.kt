package com.oborodulin.home.data.local.db.entities

import androidx.room.Entity
import java.util.*

@Entity(tableName = "payers")
class ReceiptEntity(
    var receiptDate: Date = Date(),
    var isPaid: Boolean = false
) : BaseEntity()
