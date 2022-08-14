package com.oborodulin.home.model.payer

import androidx.lifecycle.*
import com.oborodulin.home.domain.entity.Payer
import com.oborodulin.home.accounting.payer.PayerRepository
import java.util.*

class PayerViewModel : ViewModel() {
    private val payerRepo = PayerRepository.getInstance()
    private val payerIdLiveData = MutableLiveData<UUID>()
/*    var payerLiveData: LiveData<Payer?> =
        Transformations.switchMap(payerIdLiveData) { payerId ->
            payerRepo.get(payerId)
        }

 */

    fun loadPayer(payerId: UUID) {
        payerIdLiveData.value = payerId
    }

    fun savePayer(payer: Payer) {
        //payerRepo.update(payer)
    }
}