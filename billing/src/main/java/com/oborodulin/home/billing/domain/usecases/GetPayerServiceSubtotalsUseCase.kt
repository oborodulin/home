package com.oborodulin.home.billing.domain.usecases

import com.oborodulin.home.billing.domain.repositories.RatesRepository
import com.oborodulin.home.common.domain.usecases.UseCase
import com.oborodulin.home.servicing.domain.model.Service
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*

class GetPayerServiceSubtotalsUseCase(
    configuration: Configuration,
    private val ratesRepository: RatesRepository
) : UseCase<GetPayerServiceSubtotalsUseCase.Request, GetPayerServiceSubtotalsUseCase.Response>(
    configuration
) {
    override fun process(request: Request): Flow<Response> =
        ratesRepository.getSubtotalDebts(request.payerId).map {
            Response(it)
        }

    data class Request(val payerId: UUID) : UseCase.Request
    data class Response(val services: List<Service>) : UseCase.Response
}