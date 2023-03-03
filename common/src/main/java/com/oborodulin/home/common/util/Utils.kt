package com.oborodulin.home.common.util

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.RelativeSizeSpan
import android.text.style.SuperscriptSpan
import androidx.annotation.StringRes
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import timber.log.Timber
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

private const val TAG = "Common.Utils"

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

        /**
         * https://stackoverflow.com/questions/59602862/how-we-can-we-format-text-as-superscript-or-subscript-in-android-jetpack-compose
         */
        fun scriptText(
            text: String?, scripts: List<String>, isSuper: Boolean = true
        ): AnnotatedString {
            Timber.tag(TAG).d("scriptText(...) called: text = %s", text)
            if (text == null) return AnnotatedString("")
            val superscript = SpanStyle(
                baselineShift = BaselineShift.Superscript,
                fontSize = 12.sp
            )
            val subscript = SpanStyle(
                baselineShift = BaselineShift.Subscript,
                fontSize = 12.sp
            )
            for (script in scripts) {
                val scriptIdx = text.indexOf(script)
                if (scriptIdx >= 0) {
                    Timber.tag(TAG).d(
                        "scriptText(): scriptIdx = %d; text = %s; script = %s",
                        scriptIdx, text.substring(0, scriptIdx), script
                    )
                    // text.substring(scriptIdx, scriptIdx + script.length)
                    return buildAnnotatedString {
                        append(text.substring(0, scriptIdx))
                        withStyle(if (isSuper) superscript else subscript) {
                            append(script)
                        }
                    }
                }
            }
            return AnnotatedString(text)
        }

        fun toOffsetDateTime(s: String): OffsetDateTime {
            //val zoneId: ZoneId = ZoneId.of("UTC")   // Or another geographic: Europe/Paris
            //val defaultZone: ZoneId = ZoneId.systemDefault()
            val zoneId: ZoneId = ZoneId.systemDefault()
            val formatter: DateTimeFormatter =
                DateTimeFormatter.ofPattern(Constants.APP_FRACT_SEC_TIME)
            val dateTime: LocalDateTime = LocalDateTime.parse(s, formatter)
            val offset: ZoneOffset = zoneId.rules.getOffset(dateTime)
            return OffsetDateTime.of(dateTime, offset)
        }
    }
}
