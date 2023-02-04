package com.oborodulin.home.metering.ui.value

import com.oborodulin.home.common.ui.components.field.util.Focusable

enum class MeterValueFields : Focusable {
    METER_VALUE_ID,
    METERS_ID,
    METER_CURR_VALUE;

    override fun key(): String {
        return this.name
    }
}
