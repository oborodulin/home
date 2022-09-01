package com.oborodulin.home.data.local.db.entities

import androidx.room.*
import com.oborodulin.home.data.local.db.converters.DateTypeConverter
import java.math.BigDecimal
import java.util.*

@Entity(
    tableName = "meters", indices = [Index(value = ["num", "payersId", "servicesId"], unique = true)],
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
class MeterEntity(
    var num: String,
    val maxValue: BigDecimal,
    var measureUnit: String,
    @TypeConverters(DateTypeConverter::class) val passportDate: Date?,
    val verificationPeriod: Int?,
    var descr: String?,
    @ColumnInfo(index = true) var payersId: UUID,
    @ColumnInfo(index = true) var servicesId: UUID,
) : BaseEntity()