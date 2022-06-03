package com.oborodulin.home.model.rate

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.oborodulin.home.domain.rate.Rate
import com.oborodulin.home.domain.rate.RateRepository
import com.oborodulin.home.model.ListViewModel
import kotlinx.coroutines.launch

private const val TAG = "RateListViewModel"

class RateListViewModel : ListViewModel<Rate>() {
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