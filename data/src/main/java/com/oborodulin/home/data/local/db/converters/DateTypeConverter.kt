package com.oborodulin.home.data.local.db.converters

import androidx.room.TypeConverter
import com.oborodulin.home.common.util.Constants
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*

object DateTypeConverter {
    private val simpleFormatter = SimpleDateFormat("dd.MM.yyyy")
    private val offsetFormatter = DateTimeFormatter.ofPattern(Constants.APP_OFFSET_DATE_TIME)

    @TypeConverter
    fun fromDate(date: Date?): Long? = date?.let { simpleFormatter.format(it).toLong() }

    @TypeConverter
    fun toDate(millisSinceEpoch: Long?): Date? = millisSinceEpoch?.let { Date(it) }

    @TypeConverter
    @JvmStatic
    fun toOffsetDateTime(value: String?): OffsetDateTime? = value?.let {
        offsetFormatter.parse(value, OffsetDateTime::from)
        /*LocalDateTime.parse(it)
            .atZone(ZoneId.systemDefault())
            .toOffsetDateTime()*/
    }

    @TypeConverter
    @JvmStatic
    fun fromOffsetDateTime(date: OffsetDateTime?): String? = date?.format(offsetFormatter)
}