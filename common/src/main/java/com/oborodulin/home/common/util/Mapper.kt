package com.oborodulin.home.common.util

import android.content.ContentValues
import com.oborodulin.home.common.util.Constants.CONV_COEFF_BIGDECIMAL
import timber.log.Timber
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.reflect.full.memberProperties

class Mapper {
    companion object {
        private val formatter = DateTimeFormatter.ofPattern(Constants.APP_OFFSET_DATE_TIME)//.ISO_OFFSET_DATE_TIME
        /**
         * https://gist.github.com/DavidSanf0rd/9725485155bc0c4c681eb038b21c457a
         */
        fun toContentValues(instance: Any): ContentValues {
            val contentValues = ContentValues()
            instance.javaClass.kotlin.memberProperties.forEach {
                when (it.returnType.toString()) {
                    "java.time.OffsetDateTime" -> contentValues.put(it.name, (it.getter.call(instance) as OffsetDateTime).format(formatter))
                    "java.time.OffsetDateTime?" -> contentValues.put(it.name, (it.getter.call(instance) as? OffsetDateTime)?.format(formatter))
                    "java.math.BigDecimal" -> contentValues.put(it.name, (it.getter.call(instance) as BigDecimal).multiply(BigDecimal.valueOf(CONV_COEFF_BIGDECIMAL)).toLong())
                    "java.math.BigDecimal?" -> contentValues.put(it.name,(it.getter.call(instance) as? BigDecimal)?.multiply(BigDecimal.valueOf(CONV_COEFF_BIGDECIMAL))?.toLong())
                    "java.util.Date" -> contentValues.put(it.name, (it.getter.call(instance) as Date).time)
                    "java.util.Date?" -> contentValues.put(it.name, (it.getter.call(instance) as? Date)?.time)
                    "kotlin.Boolean" -> contentValues.put(it.name, (it.getter.call(instance) as Boolean).let{ b -> if (b) 1 else 0 })
                    "kotlin.Boolean?" -> contentValues.put(it.name, (it.getter.call(instance) as? Boolean)?.let{ b -> if (b) 1 else 0 })
                    else -> contentValues.put(it.name, it.getter.call(instance)?.toString())
                }
                Timber.d("%s --[%s]---> %s", it.name, it.returnType, contentValues[it.name])
            }
            return contentValues
        }
    }
}