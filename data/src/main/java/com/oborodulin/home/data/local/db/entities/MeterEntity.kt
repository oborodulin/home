package com.oborodulin.home.data.local.db.entities

import androidx.room.*
import com.oborodulin.home.data.local.db.converters.DateTypeConverter
import java.math.BigDecimal
import java.util.*

@Entity(
    tableName = MeterEntity.TABLE_NAME,
    indices = [Index(value = ["num", "payersServicesId"], unique = true)],
    foreignKeys = [ForeignKey(
        entity = PayerServiceCrossRefEntity::class,
        parentColumns = arrayOf("payerServiceId"),
        childColumns = arrayOf("payersServicesId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class MeterEntity(
    @PrimaryKey var meterId: UUID = UUID.randomUUID(),
    var num: String,
    val maxValue: BigDecimal,
    @TypeConverters(DateTypeConverter::class) val passportDate: Date? = null,
    val verificationPeriod: Int? = null,
    @ColumnInfo(index = true) var payersServicesId: UUID,
) {
    companion object {
        const val TABLE_NAME = "meters"
    }
}