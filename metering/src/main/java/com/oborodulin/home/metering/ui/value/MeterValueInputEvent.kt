package com.oborodulin.home.metering.ui.value

import com.oborodulin.home.common.ui.components.field.util.Inputable

sealed class MeterValueInputEvent(val id: String, val value: String) : Inputable {
    data class CurrentValue(val key: String, val input: String) : MeterValueInputEvent(key, input)

    override fun value(): String {
        return this.value
    }
}
