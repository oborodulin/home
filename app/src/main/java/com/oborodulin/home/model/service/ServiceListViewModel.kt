package com.oborodulin.home.model.service

import com.oborodulin.home.data.local.db.entities.ServiceEntity
import com.oborodulin.home.model.ListViewModel

private const val TAG = "ServiceListViewModel"

class ServiceListViewModel : ListViewModel<ServiceEntity>() {
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