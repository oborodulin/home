package com.oborodulin.home.domain.meter

import androidx.room.*
import com.oborodulin.home.database.DateTypeConverter
import com.oborodulin.home.domain.BaseEntity
import com.oborodulin.home.domain.service.Service
import java.math.BigDecimal
import java.util.*

@Entity(
    tableName = "meters", indices = [Index(value = ["num", "servicesId"], unique = true)],
    foreignKeys = [ForeignKey(
        entity = Service::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("servicesId"),
        onDelete = ForeignKey.CASCADE
    )]
)
class Meter(
    var num: String,
    val maxValue: BigDecimal,
    var measureUnit: String,
    @TypeConverters(DateTypeConverter::class) val verificationDate: Date?,
    val verificationPeriod: Int?,
    var descr: String?,
    @ColumnInfo(index = true) var servicesId: UUID,
) : BaseEntity()