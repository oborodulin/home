package com.oborodulin.home.servicing.domain.usecases.service

import com.oborodulin.home.common.domain.usecases.UseCase
import com.oborodulin.home.servicing.domain.model.Service
import com.oborodulin.home.servicing.domain.repositories.ServicesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetServicesUseCase(
    configuration: Configuration,
    private val servicesRepository: ServicesRepository
) : UseCase<GetServicesUseCase.Request, GetServicesUseCase.Response>(configuration) {

    override fun process(request: Request): Flow<Response> =
        servicesRepository.getAllServices().map {
            Response(it)
        }

    object Request : UseCase.Request
    data class Response(val services: List<Service>) : UseCase.Response
}