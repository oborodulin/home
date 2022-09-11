package com.oborodulin.home.data.local.db.entities

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import com.oborodulin.home.data.local.db.converters.DateTypeConverter
import java.math.BigDecimal
import java.util.*

@Entity(
    tableName = RateEntity.TABLE_NAME,
    indices = [Index(
        value = ["startDate", "fromMeterValue", "isPerPerson", "servicesId", "payerServicesId"],
        unique = true
    )],
    foreignKeys = [ForeignKey(
        entity = ServiceEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("servicesId"),
        onDelete = CASCADE
    ), ForeignKey(
        entity = PayerServiceEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("payerServicesId"),
        onDelete = CASCADE
    )]
)
class RateEntity(
    @TypeConverters(DateTypeConverter::class) val startDate: Date = Date(),
    val fromMeterValue: BigDecimal? = null,
    val toMeterValue: BigDecimal? = null,
    val rateValue: BigDecimal,
    val isPerPerson: Boolean = false,
    @ColumnInfo(index = true) var servicesId: UUID,
    @ColumnInfo(index = true) var payerServicesId: UUID? = null,
) : BaseEntity() {
    companion object {
        const val TABLE_NAME = "rates"
    }
}
