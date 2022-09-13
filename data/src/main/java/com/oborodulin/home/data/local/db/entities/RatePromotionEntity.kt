package com.oborodulin.home.data.local.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import java.util.*

@Entity(
    tableName = RatePromotionEntity.TABLE_NAME,
    foreignKeys = [ForeignKey(
        entity = RateEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("ratesId"),
        onDelete = ForeignKey.CASCADE
    )]
)
class RatePromotionEntity(
    val paymentMonths: Int = 1, // период оплаты - кол-во месяцев в году
    val isPrevRate: Boolean = false, // считаем по предыдущему тарифу
    val isActive: Boolean = true,
    @ColumnInfo(index = true) var ratesId: UUID,
) : BaseEntity() {
    companion object {
        const val TABLE_NAME = "rate_promotions"
    }
}