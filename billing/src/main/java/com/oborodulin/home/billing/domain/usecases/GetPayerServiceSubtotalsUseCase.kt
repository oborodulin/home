package com.oborodulin.home.billing.domain.usecases

import com.oborodulin.home.billing.domain.model.PayerServiceSubtotal
import com.oborodulin.home.billing.domain.repositories.BillingRepository
import com.oborodulin.home.common.domain.usecases.UseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class GetPayerServiceSubtotalsUseCase(
    configuration: Configuration,
    private val billingRepository: BillingRepository
) : UseCase<GetPayerServiceSubtotalsUseCase.Request, GetPayerServiceSubtotalsUseCase.Response>(
    configuration
) {
    override fun process(request: Request): Flow<Response> =
        billingRepository.getSubtotalDebts(request.payerId).map {
            Response(it)
        }

    data class Request(val payerId: UUID) : UseCase.Request
    data class Response(val payerServiceSubtotals: List<PayerServiceSubtotal>) : UseCase.Response
}