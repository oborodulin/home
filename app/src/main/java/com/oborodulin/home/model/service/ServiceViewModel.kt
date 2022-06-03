package com.oborodulin.home.model.service

import androidx.lifecycle.*
import com.oborodulin.home.domain.service.Service
import com.oborodulin.home.domain.service.ServiceRepository
import kotlinx.coroutines.launch
import java.util.*

class ServiceViewModel : ViewModel() {
    private val serviceRepo = ServiceRepository.getInstance(viewModelScope)
    private val serviceIdLiveData: MutableLiveData<UUID> by lazy { MutableLiveData<UUID>() }

    var serviceLiveData: LiveData<Service?> =
        Transformations.switchMap(serviceIdLiveData) { serviceId ->
            serviceRepo.get(serviceId)
        }

    fun loadService(serviceId: UUID) {
        serviceIdLiveData.value = serviceId
    }

    fun saveService(service: Service) {
        viewModelScope.launch {
            serviceRepo.update(service)
        }
    }

    fun nextDisplayPos(): LiveData<Int> = serviceRepo.nextDisplayPos()
}