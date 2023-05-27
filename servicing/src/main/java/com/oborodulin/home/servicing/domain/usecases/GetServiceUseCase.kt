package com.oborodulin.home.servicing.domain.usecases

import com.oborodulin.home.common.domain.usecases.UseCase
import com.oborodulin.home.servicing.domain.model.Service
import com.oborodulin.home.servicing.domain.repositories.ServicesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class GetServiceUseCase(
    configuration: Configuration,
    private val servicesRepository: ServicesRepository
) : UseCase<GetServiceUseCase.Request, GetServiceUseCase.Response>(configuration) {

    override fun process(request: Request): Flow<Response> =
        servicesRepository.get(request.id).map {
            Response(it)
        }

    data class Request(val id: UUID) : UseCase.Request
    data class Response(val service: Service) : UseCase.Response
}