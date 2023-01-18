package com.oborodulin.home.common.util

import android.content.ContentValues
import timber.log.Timber
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.reflect.full.memberProperties

class Mapper {
    companion object {
        private val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        /**
         * https://gist.github.com/DavidSanf0rd/9725485155bc0c4c681eb038b21c457a
         */
        fun toContentValues(instance: Any): ContentValues {
            val contentValues = ContentValues()
            instance.javaClass.kotlin.memberProperties.forEach {
                Timber.d(
                    "${it.name} --[${it.returnType}]---> ${
                        it.getter.call(instance)?.toString()
                    }"
                )
                when (it.returnType.toString()) {
                    "java.util.Date" -> contentValues.put(it.name, (it.getter.call(instance) as Date).time)
                    "java.util.Date?" -> contentValues.put(it.name, (it.getter.call(instance) as? Date)?.time)
                    "java.time.OffsetDateTime" -> contentValues.put(it.name, (it.getter.call(instance) as OffsetDateTime).format(formatter))
                    "java.time.OffsetDateTime?" -> contentValues.put(it.name, (it.getter.call(instance) as? OffsetDateTime)?.format(formatter))
                    else -> contentValues.put(it.name, it.getter.call(instance)?.toString())
                }
            }
            return contentValues
        }
    }
}