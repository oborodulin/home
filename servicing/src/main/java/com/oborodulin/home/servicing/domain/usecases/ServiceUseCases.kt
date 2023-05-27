package com.oborodulin.home.servicing.domain.usecases

data class ServiceUseCases(
    val getServiceUseCase: GetServiceUseCase,
    val getServicesUseCase: GetServicesUseCase,
    val saveServiceUseCase: SaveServiceUseCase,
    val deleteServiceUseCase: DeleteServiceUseCase,
)
