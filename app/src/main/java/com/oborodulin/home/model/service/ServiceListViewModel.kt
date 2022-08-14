package com.oborodulin.home.model.service

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.oborodulin.home.domain.entity.Service
import com.oborodulin.home.domain.service.ServiceRepository
import com.oborodulin.home.model.ListViewModel
import kotlinx.coroutines.launch

private const val TAG = "ServiceListViewModel"

class ServiceListViewModel : ListViewModel<Service>() {
 /*   private val serviceRepo = ServiceRepository.getInstance(viewModelScope)
    val servicesLiveData = serviceRepo.getAll()

    init {
        Log.d(TAG, "ServiceListViewModel instance created")
    }

    fun addService(service: Service) {
        viewModelScope.launch {
            serviceRepo.add(service)
        }
    }

    fun deleteService(service: Service) {
        viewModelScope.launch {
            serviceRepo.delete(service)
        }
    }

    override fun addItem(entity: Service) {
        addService(entity)
    }

    override fun deleteItem(entity: Service) {
        deleteService(entity)
    }

    fun nextDisplayPos(): LiveData<Int> = serviceRepo.nextDisplayPos()

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "ServiceListViewModel instance about to be destroyed")
    }

  */
}