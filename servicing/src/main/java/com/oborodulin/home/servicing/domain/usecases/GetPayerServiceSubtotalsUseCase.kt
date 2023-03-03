package com.oborodulin.home.servicing.domain.usecases

import com.oborodulin.home.common.domain.usecases.UseCase
import com.oborodulin.home.servicing.domain.model.Service
import com.oborodulin.home.servicing.domain.repositories.ServicesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*

class GetPayerServiceSubtotalsUseCase(
    configuration: Configuration,
    private val servicesRepository: ServicesRepository
) : UseCase<GetPayerServiceSubtotalsUseCase.Request, GetPayerServiceSubtotalsUseCase.Response>(
    configuration
) {
    override fun process(request: Request): Flow<Response> =
        servicesRepository.getSubtotalDebts(request.payerId).map {
            Response(it)
        }

    data class Request(val payerId: UUID) : UseCase.Request
    data class Response(val services: List<Service>) : UseCase.Response
}