package com.oborodulin.home.data.local.db.entities

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import com.oborodulin.home.data.local.db.converters.DateTypeConverter
import java.math.BigDecimal
import java.util.*

@Entity(
    tableName = "rates", indices = [Index(value = ["startDate", "servicesId"], unique = true)],
    foreignKeys = [ForeignKey(
        entity = ServiceEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("servicesId"),
        onDelete = CASCADE
    )]
)
class RateEntity(
    @TypeConverters(DateTypeConverter::class) val startDate: Date = Date(),
    val fromMeterValue: BigDecimal? = null,
    val toMeterValue: BigDecimal? = null,
    val rateValue: BigDecimal,
    @ColumnInfo(index = true) var servicesId: UUID,
) : BaseEntity()
