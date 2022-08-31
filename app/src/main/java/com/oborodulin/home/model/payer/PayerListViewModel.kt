package com.oborodulin.home.model.payer

import com.oborodulin.home.domain.entity.Payer
import com.oborodulin.home.model.ListViewModel

private const val TAG = "PayerListViewModel"

class PayerListViewModel : ListViewModel<Payer>() {
 /*   private val payerRepo = PayerRepository.getInstance()
   val payersLiveData = payerRepo.getAll()

    fun addPayer(payer: Payer) {
        payerRepo.add(payer)
    }

    fun deletePayer(payer: Payer) {
        payerRepo.delete(payer)
    }

    override fun addItem(entity: Payer) {
        addPayer(entity)
    }

    override fun deleteItem(entity: Payer) {
        deletePayer(entity)
    }
*/
//    val payers = mutableListOf<Payer>()

/*    init {
        val payer1 = Payer()
        payer1.ercCode = "100010888822587"
        payer1.fullName = "Чубенко Л.П."
        payer1.address = "г. Донецк-4, пр-т Киевский, д. 1-б, кв. 147"
        payer1.personsNum = 2
        payer1.totalArea = BigDecimal(52.5)
        payer1.livingSpace = BigDecimal(48.7)
        payer1.heatedVolume = BigDecimal(121.75)
        payers += payer1

        val payer2 = Payer()
        payer2.ercCode = "100011804669003"
        payer2.fullName = "Бородулина З.В."
        payer2.address = "г. Донецк, пр-т Партизанский, д. 64, кв. 39"
        payer2.personsNum = 1
        payer2.totalArea = BigDecimal(52.5)
        payer2.livingSpace = BigDecimal(48.7)
        payer2.heatedVolume = BigDecimal(121.75)

        payers += payer2
    }
 */
}