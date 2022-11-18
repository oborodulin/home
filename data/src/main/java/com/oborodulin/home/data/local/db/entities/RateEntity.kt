package com.oborodulin.home.data.local.db.entities

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import com.oborodulin.home.data.local.db.converters.DateTypeConverter
import java.math.BigDecimal
import java.util.*

@Entity(
    tableName = RateEntity.TABLE_NAME,
    indices = [Index(
        value = ["startDate", "fromMeterValue", "isPerPerson", "isPrivileges", "servicesId", "payersServicesId"],
        unique = true
    )],
    foreignKeys = [ForeignKey(
        entity = ServiceEntity::class,
        parentColumns = arrayOf("serviceId"),
        childColumns = arrayOf("servicesId"),
        onDelete = CASCADE
    ), ForeignKey(
        entity = PayerServiceCrossRefEntity::class,
        parentColumns = arrayOf("payerServiceId"),
        childColumns = arrayOf("payersServicesId"),
        onDelete = CASCADE
    )]
)
class RateEntity(
    @PrimaryKey var rateId: UUID = UUID.randomUUID(),
    @TypeConverters(DateTypeConverter::class) val startDate: Date = Date(),
    val fromMeterValue: BigDecimal? = null,
    val toMeterValue: BigDecimal? = null,
    val rateValue: BigDecimal,
    val isPerPerson: Boolean = false, // считаем по норме на 1 человека, но приоритет для тарифов по счётчику
    val isPrivileges: Boolean = false, // считаем по счётчику, но по льготному тарифу
    @ColumnInfo(index = true) var servicesId: UUID,
    @ColumnInfo(index = true) var payersServicesId: UUID? = null,
) {
    companion object {
        const val TABLE_NAME = "rates"
    }
}
