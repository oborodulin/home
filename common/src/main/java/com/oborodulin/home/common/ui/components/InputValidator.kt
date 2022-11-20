package com.oborodulin.home.common.ui.components

import com.oborodulin.home.common.R

object InputValidator {

    fun getNameErrorIdOrNull(input: String): Int? {
        return when {
            input.length < 2 -> R.string.name_too_short_error
            //etc..
            else -> null
        }
    }

    fun getCardNumberErrorIdOrNull(input: String): Int? {
        return when {
            input.length < 16 -> R.string.card_number_too_short_error
            //etc..
            else -> null
        }
    }

}
