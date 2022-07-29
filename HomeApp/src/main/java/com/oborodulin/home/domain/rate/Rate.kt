package com.oborodulin.home.domain.rate

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import com.oborodulin.home.database.DateTypeConverter
import com.oborodulin.home.domain.BaseEntity
import com.oborodulin.home.domain.service.Service
import java.math.BigDecimal
import java.util.*

@Entity(
    tableName = "rates", indices = [Index(value = ["startDate", "servicesId"], unique = true)],
    foreignKeys = [ForeignKey(
        entity = Service::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("servicesId"),
        onDelete = CASCADE
    )]
)
class Rate(
    @TypeConverters(DateTypeConverter::class) val startDate: Date = Date(),
    val fromMeterValue: BigDecimal? = null,
    val toMeterValue: BigDecimal? = null,
    val rateValue: BigDecimal,
    @ColumnInfo(index = true) var servicesId: UUID,
) : BaseEntity()