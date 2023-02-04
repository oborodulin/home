package com.oborodulin.home.metering.ui.value

import com.oborodulin.home.common.ui.components.field.util.Validatable

private const val TAG = "Accounting.ui.MeterValueInputValidator"

sealed class MeterValueInputValidator : Validatable {
    object CurrentValue : MeterValueInputValidator() {
        override fun errorIdOrNull(input: String): Int? =
            when {
                input.isNotEmpty() -> if (input.toBigDecimalOrNull() == null) com.oborodulin.home.common.R.string.number_negative_error else null
                else -> null
            }
    }
}
