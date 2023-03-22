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
import com.neovisionaries.i18n.CountryCode
import timber.log.Timber
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

private const val TAG = "Common.Utils"

class Utils {
    companion object {
        var currencyLocaleMap: SortedMap<Currency, Locale> =
            sortedMapOf(compareBy { it.currencyCode })

        init {
            for (locale in Locale.getAvailableLocales()) {
                try {
                    val cc: CountryCode? = CountryCode.getByCode(locale.country)
                    cc?.let {
                        val currency = it.currency
                        Timber.tag(TAG).d(
                            "init(): language: %s, country: %s; alpha2: %s; currency code: %s; currency symbol: %s",
                            locale.language, locale.country,
                            it.alpha2, it.currency.currencyCode, it.currency.symbol
                        )
                        // val currency = Currency.getInstance(Locale(locale.language, it.alpha2))
                        // Locale locale = code . toLocale ();
                        // code = CountryCode.getByLocale(locale);
                        currencyLocaleMap[currency] = locale
                    }
                } catch (e: Exception) {
                    Timber.tag(TAG).e(e)
                }
            }
        }

        fun currencyCode(locale: Locale = Locale.getDefault()): String {
/*          val currency: Currency = Currency.getInstance(currencyCode)
            Timber.tag(TAG).d(
                "currencySymbol(): %s:- %s",
                currencyCode, currency.getSymbol(currencyLocaleMap[currency])
            )
            return currency.getSymbol(currencyLocaleMap[currency])
            CountryCode.getByCode(locale.country).currency.currencyCode
 */
            return "руб."
        }

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
                DateTimeFormatter.ofPattern(Constants.APP_OFFSET_DATE_TIME)
            val dateTime: LocalDateTime = LocalDateTime.parse(s, formatter)
            val offset: ZoneOffset = zoneId.rules.getOffset(dateTime)
            return OffsetDateTime.of(dateTime, offset)
        }
    }
}
