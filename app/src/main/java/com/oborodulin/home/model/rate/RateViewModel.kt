package com.oborodulin.home.model.rate

import androidx.lifecycle.*
//import com.oborodulin.home.domain.entity.Rate
import com.oborodulin.home.domain.rate.RateRepository
import kotlinx.coroutines.launch
import java.util.*

class RateViewModel : ViewModel() {
    /*
    private val rateRepository = RateRepository.getInstance(viewModelScope)
    private val rateIdLiveData = MutableLiveData<UUID>()
    var rateLiveData: LiveData<Rate?> =
        Transformations.switchMap(rateIdLiveData) { rateId ->
            rateRepository.get(rateId)
        }

    fun loadRate(rateId: UUID) {
        rateIdLiveData.value = rateId
    }

    fun saveRate(rate: Rate) {
        viewModelScope.launch {
            rateRepository.update(rate)
        }
    }

     */
}