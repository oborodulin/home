package com.oborodulin.home.data.local.db.converters

import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.util.*

class DateTypeConverter {
    private val formatter = SimpleDateFormat("dd.MM.yyyy")

    @TypeConverter
    fun fromDate(date: Date?): Long? = date?.let { formatter.format(it).toLong() }

    @TypeConverter
    fun toDate(millisSinceEpoch: Long?): Date? = millisSinceEpoch?.let { Date(it) }
}