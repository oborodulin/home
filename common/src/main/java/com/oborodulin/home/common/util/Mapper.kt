package com.oborodulin.home.common.util

import android.content.ContentValues
import timber.log.Timber
import kotlin.reflect.full.memberProperties

class Mapper {
    companion object {
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
                contentValues.put(it.name, it.getter.call(instance)?.toString())
            }
            return contentValues
        }
    }
}