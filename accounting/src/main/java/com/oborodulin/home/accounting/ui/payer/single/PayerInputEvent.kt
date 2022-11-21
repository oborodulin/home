package com.oborodulin.home.accounting.ui.payer.single

import com.oborodulin.home.common.ui.components.field.Inputable

sealed class PayerInputEvent(val value: String) : Inputable {
    data class ErcCode(val input: String) : PayerInputEvent(input)
    data class FullName(val input: String) : PayerInputEvent(input)

    override fun value(): String {
        return this.value
    }
}
