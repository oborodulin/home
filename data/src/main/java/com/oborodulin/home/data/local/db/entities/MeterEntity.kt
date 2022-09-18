package com.oborodulin.home.data.local.db.entities

import androidx.room.*
import com.oborodulin.home.data.local.db.converters.DateTypeConverter
import java.math.BigDecimal
import java.util.*

@Entity(
    tableName = MeterEntity.TABLE_NAME,
    indices = [Index(value = ["num", "payerServicesId"], unique = true)],
    foreignKeys = [ForeignKey(
        entity = PayerServiceCrossRefEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("payerServicesId"),
        onDelete = ForeignKey.CASCADE
    )]
)
class MeterEntity(
    var num: String,
    val maxValue: BigDecimal,
    //var measureUnit: String? = null,
    @TypeConverters(DateTypeConverter::class) val passportDate: Date? = null,
    val verificationPeriod: Int? = null,
    //var descr: String? = null,
    @ColumnInfo(index = true) var payerServicesId: UUID,
) : BaseEntity() {
    companion object {
        const val TABLE_NAME = "meters"
    }
}