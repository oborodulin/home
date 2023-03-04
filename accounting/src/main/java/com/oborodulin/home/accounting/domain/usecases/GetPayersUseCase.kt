package com.oborodulin.home.accounting.domain.usecases

import com.oborodulin.home.billing.domain.repositories.RatesRepository
import com.oborodulin.home.common.domain.usecases.UseCase
import com.oborodulin.home.domain.model.Payer
import com.oborodulin.home.domain.repositories.PayersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetPayersUseCase(
    configuration: Configuration,
    private val payersRepository: PayersRepository,
    private val ratesRepository: RatesRepository
) : UseCase<GetPayersUseCase.Request, GetPayersUseCase.Response>(configuration) {

    override fun process(request: Request): Flow<Response> =
        combine(payersRepository.getAll(), ratesRepository.getTotalDebts()) { payers, totals ->
            Response(payers.map { payer ->
                val payerTotal: Payer? = totals.first { it.id == payer.id }
                payerTotal?.let {
                    with(payer) {
                        fromPaymentDate = payerTotal.fromPaymentDate
                        toPaymentDate = payerTotal.toPaymentDate
                        totalDebt = payerTotal.totalDebt
                    }
                }
                payer
            })
        }

    object Request : UseCase.Request
    data class Response(val payers: List<Payer>) : UseCase.Response
}