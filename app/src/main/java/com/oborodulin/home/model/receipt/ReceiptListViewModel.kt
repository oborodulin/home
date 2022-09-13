package com.oborodulin.home.model.receipt

import androidx.lifecycle.ViewModel
import com.oborodulin.home.data.local.db.entities.ReceiptEntity
import java.util.*

class ReceiptListViewModel : ViewModel() {
    val receiptEntities = mutableListOf<ReceiptEntity>()

    init {
        var year: Int = 2021
/*        for (i in 0 until 15) {
            val receiptEntity = ReceiptEntity()
            if (i % 12 == 0) year++
            receiptEntity.receiptDate = getDate(year, i % 12, 1)
            receiptEntity.isPaid = i % 2 == 0
            receiptEntities += receiptEntity

        }
 */
    }

    private fun getDate(year: Int, month: Int, day: Int): Date {
        val cal: Calendar = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month)
        cal.set(Calendar.DAY_OF_MONTH, day)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.time
    }
}