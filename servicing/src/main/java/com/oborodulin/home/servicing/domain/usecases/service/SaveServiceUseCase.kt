package com.oborodulin.home.servicing.domain.usecases.service

import com.oborodulin.home.common.domain.usecases.UseCase
import com.oborodulin.home.common.domain.usecases.UseCaseException
import com.oborodulin.home.servicing.domain.model.Service
import com.oborodulin.home.servicing.domain.repositories.ServicesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class SaveServiceUseCase(
    configuration: Configuration,
    private val servicesRepository: ServicesRepository
) : UseCase<SaveServiceUseCase.Request, SaveServiceUseCase.Response>(configuration) {

    override fun process(request: Request): Flow<Response> {
        return servicesRepository.saveService(request.service)
            .map {
                Response(it)
            }.catch { throw UseCaseException.PayerSaveException(it) }
    }

    data class Request(val service: Service) : UseCase.Request
    data class Response(val service: Service) : UseCase.Response
}
