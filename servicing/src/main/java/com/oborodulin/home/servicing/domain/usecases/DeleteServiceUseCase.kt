package com.oborodulin.home.servicing.domain.usecases

import com.oborodulin.home.common.domain.usecases.UseCase
import com.oborodulin.home.servicing.domain.repositories.ServicesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class DeleteServiceUseCase(
    configuration: Configuration,
    private val servicesRepository: ServicesRepository
) : UseCase<DeleteServiceUseCase.Request, DeleteServiceUseCase.Response>(configuration) {

    override fun process(request: Request): Flow<Response> {
        return servicesRepository.deleteById(request.serviceId)
            .map {
                Response
            }
    }

    data class Request(val serviceId: UUID) : UseCase.Request
    object Response : UseCase.Response
}
