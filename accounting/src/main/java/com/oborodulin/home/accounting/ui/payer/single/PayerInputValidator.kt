package com.oborodulin.home.accounting.ui.payer.single

import com.oborodulin.home.accounting.R
import com.oborodulin.home.common.ui.components.field.Validatable

private const val TAG = "Accounting.ui.PayerInputValidator"

sealed class PayerInputValidator : Validatable {
    object ErcCode : PayerInputValidator() {
        override fun errorIdOrNull(input: String): Int? =
            when {
                input.isEmpty() -> R.string.erc_code_empty_error
                //etc..
                else -> null
            }
    }

    object FullName : PayerInputValidator() {
        override fun errorIdOrNull(input: String): Int? =
            when {
                input.isEmpty() -> R.string.full_name_empty_error
                else -> null
            }
    }

    object Address : PayerInputValidator() {
        override fun errorIdOrNull(input: String): Int? =
            when {
                input.isEmpty() -> R.string.address_empty_error
                else -> null
            }
    }

    object TotalArea : PayerInputValidator() {
        override fun errorIdOrNull(input: String): Int? =
            when {
                input.isNotEmpty() -> if (input.toBigDecimalOrNull() == null) com.oborodulin.home.common.R.string.number_negative_error else null
                else -> null
            }
    }

    object LivingSpace : PayerInputValidator() {
        override fun errorIdOrNull(input: String): Int? =
            when {
                input.isNotEmpty() -> if (input.toBigDecimalOrNull() == null) com.oborodulin.home.common.R.string.number_negative_error else null
                else -> null
            }
    }

    object HeatedVolume : PayerInputValidator() {
        override fun errorIdOrNull(input: String): Int? =
            when {
                input.isNotEmpty() -> if (input.toBigDecimalOrNull() == null) com.oborodulin.home.common.R.string.number_negative_error else null
                else -> null
            }
    }
}
