package com.oborodulin.home.accounting.ui.payer.single

import com.oborodulin.home.common.R
import com.oborodulin.home.common.ui.components.field.Validatable

sealed class PayerInputValidator : Validatable {
    object ErcCode : PayerInputValidator() {
        override fun errorIdOrNull(input: String): Int? =
            when {
                input.length < 2 -> R.string.name_too_short_error
                //etc..
                else -> null
            }
    }

    object FullName : PayerInputValidator() {
        override fun errorIdOrNull(input: String): Int? =
            when {
                input.length < 16 -> R.string.card_number_too_short_error
                //etc..
                else -> null
            }
    }
}
