package com.oborodulin.home.model.rate

private const val TAG = "RateListViewModel"
/*
class RateListViewModel : ListViewModel<RateEntity>() {
 private val rateRepository = RateRepository.getInstance(viewModelScope)
   val ratesLiveData = rateRepository.getAll()

   init {
       Log.d(TAG, "ViewModel instance created")
   }

   private fun addRate(rate: Rate) {
       viewModelScope.launch {
           rateRepository.add(rate)
       }
   }

   private fun deleteRate(rate: Rate) {
       viewModelScope.launch {
           rateRepository.delete(rate)
       }
   }

   override fun addItem(entity: Rate) {
       addRate(entity)
   }

   override fun deleteItem(entity: Rate) {
       deleteRate(entity)
   }

   override fun onCleared() {
       super.onCleared()
       Log.d(TAG, "ViewModel instance about to be destroyed")
   }

}
*/