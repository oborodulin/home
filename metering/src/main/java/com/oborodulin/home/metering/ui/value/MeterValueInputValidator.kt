package com.oborodulin.home.metering.ui.value

import com.oborodulin.home.common.ui.components.field.util.Validatable
import timber.log.Timber
import kotlin.concurrent.thread

private const val TAG = "Metering.ui.MeterValueInputValidator"

sealed class MeterValueInputValidator : Validatable {
    object CurrentValue : MeterValueInputValidator() {
        override fun errorIdOrNull(vararg inputs: String): Int? =
            when {
                /*
                inputs.size != 3 -> {
                    val errMsg =
                        "CurrentValue.errorIdOrNull(): inputs.size = %d".format(inputs.size)
                    Timber.tag(TAG).e(errMsg)
                    thread(name = "WatchdogThread") {
                        throw IllegalArgumentException(errMsg)
                    }
                    null
                }
                 */
                inputs[0].isNotEmpty() -> {
                    val value = inputs[0].replace(',', '.').toBigDecimalOrNull()
                    //val prevValue = inputs[1].toBigDecimalOrNull()
                    //val maxValue = inputs[2].toBigDecimalOrNull()
                    when {
                        value == null -> com.oborodulin.home.common.R.string.number_negative_error
/*
                        prevValue != null ->
                            when {
                                value < prevValue -> com.oborodulin.home.common.R.string.number_negative_error
                                else -> null
                            }

 */
                        else -> null
                    }
                }
                else -> null
            }
    }
}
