package com.oborodulin.home.common.util

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.RelativeSizeSpan
import android.text.style.SuperscriptSpan
import androidx.annotation.StringRes

class Text {
    companion object {
        private fun superscriptText(@StringRes resId: Int, s: String): CharSequence {
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
    }
}
