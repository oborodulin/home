package com.oborodulin.home.accounting.ui.meter

import com.oborodulin.home.common.R
import com.oborodulin.home.common.ui.components.field.Validatable

private const val TAG = "Accounting.ui.MeterValueInputValidator"

sealed class MeterValueInputValidator : Validatable {
    object CurrentValue : MeterValueInputValidator() {
        override fun errorIdOrNull(input: String): Int? =
        when
        {
            input.length < 2 -> R.string.name_too_short_error
            //etc..
            else -> null
        }
    }
}
