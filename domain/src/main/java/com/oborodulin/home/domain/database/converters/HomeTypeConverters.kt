package com.oborodulin.home.domain.database.converters

import androidx.room.TypeConverter
import java.math.BigDecimal
import java.util.*

class HomeTypeConverters {
    @TypeConverter
    fun toUUID(uuid: String?): UUID? = UUID.fromString(uuid)

    @TypeConverter
    fun fromUUID(uuid: UUID?): String? = uuid?.toString()

    @TypeConverter
    fun fromDateTime(date: Date?): Long? = date?.time

    @TypeConverter
    fun toDateTime(millisSinceEpoch: Long?): Date? = millisSinceEpoch?.let { Date(it) }

    @TypeConverter
    fun toBigDecimal(value: Long?): BigDecimal? =
        value?.let { BigDecimal(it).divide(BigDecimal(1000)) }

    @TypeConverter
    fun fromBigDecimal(bigDecimal: BigDecimal?): Long? =
        bigDecimal?.multiply(BigDecimal(1000))?.toLong()
}