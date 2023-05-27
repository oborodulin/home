package com.oborodulin.home.servicing.ui

import java.math.BigDecimal

sealed class ServicingUiEvent {
    data class ErcCodeChanged(val ercCode: String) : ServicingUiEvent()
    data class FullNameChanged(val fullName: String) : ServicingUiEvent()
    data class AddressChanged(val address: String) : ServicingUiEvent()
    data class TotalAreaChanged(val totalArea: BigDecimal?) : ServicingUiEvent()
    data class LivingSpaceChanged(val livingSpace: BigDecimal?) : ServicingUiEvent()
    data class HeatedVolumeChanged(val heatedVolume: BigDecimal?) : ServicingUiEvent()
    data class PaymentDayChanged(val paymentDay: Int?) : ServicingUiEvent()
    data class PersonsNumChanged(val personsNum: Int?) : ServicingUiEvent()
    object Submit : ServicingUiEvent()
}
