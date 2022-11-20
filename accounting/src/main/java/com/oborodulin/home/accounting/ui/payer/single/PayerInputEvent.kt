package com.oborodulin.home.accounting.ui.payer.single


sealed class PayerInputEvent {
    class ErcCode(val input: String) : PayerInputEvent()
    class FullName(val input: String) : PayerInputEvent()
}
