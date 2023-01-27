package com.oborodulin.home.accounting.ui.payer.single

import com.oborodulin.home.common.ui.components.field.Inputable

sealed class PayerInputEvent(val value: String) : Inputable {
    data class ErcCode(val input: String) : PayerInputEvent(input)
    data class FullName(val input: String) : PayerInputEvent(input)
    data class Address(val input: String) : PayerInputEvent(input)
    data class TotalArea(val input: String) : PayerInputEvent(input)
    data class LivingSpace(val input: String) : PayerInputEvent(input)
    data class HeatedVolume(val input: String) : PayerInputEvent(input)
    data class PaymentDay(val input: String) : PayerInputEvent(input)
    data class PersonsNum(val input: String) : PayerInputEvent(input)

    override fun value(): String {
        return this.value
    }
}
