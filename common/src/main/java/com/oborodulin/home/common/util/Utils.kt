package com.oborodulin.home.common.util

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.RelativeSizeSpan
import android.text.style.SuperscriptSpan
import androidx.annotation.StringRes
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class Utils {
    companion object {
        fun superscriptText(@StringRes resId: Int, s: String): CharSequence {
            val resString = ""//resources.getString(resId)
            val strSpan = SpannableStringBuilder(resString)

            strSpan.setSpan(
                SuperscriptSpan(), resString.indexOf(s),
                resString.indexOf(s) + s.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            strSpan.setSpan(
                RelativeSizeSpan(0.5f), resString.indexOf(s),
                resString.indexOf(s) + s.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            return strSpan
        }

        fun toOffsetDateTime(s: String): OffsetDateTime {
            //val zoneId: ZoneId = ZoneId.of("UTC")   // Or another geographic: Europe/Paris
            //val defaultZone: ZoneId = ZoneId.systemDefault()
            val zoneId: ZoneId = ZoneId.systemDefault()
            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
            val dateTime: LocalDateTime = LocalDateTime.parse(s, formatter)
            val offset: ZoneOffset = zoneId.rules.getOffset(dateTime)
            return OffsetDateTime.of(dateTime, offset)
        }
    }
}
