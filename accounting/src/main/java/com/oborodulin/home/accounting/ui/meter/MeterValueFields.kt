package com.oborodulin.home.accounting.ui.meter

import com.oborodulin.home.common.ui.components.field.Focusable

enum class MeterValueFields : Focusable {
    METER_VALUE_ID,
    METERS_ID,
    METER_CURR_VALUE;

    override fun key(): String {
        return this.name
    }
}
