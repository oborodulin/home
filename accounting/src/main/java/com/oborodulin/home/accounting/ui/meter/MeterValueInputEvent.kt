package com.oborodulin.home.accounting.ui.meter

import com.oborodulin.home.common.ui.components.field.Inputable

sealed class MeterValueInputEvent(val value: String) : Inputable {
    data class CurrentValue(val input: String) : MeterValueInputEvent(input)

    override fun value(): String {
        return this.value
    }
}
