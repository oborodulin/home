package com.oborodulin.home.accounting.domain.usecases

import com.oborodulin.home.accounting.domain.repositories.PayersRepository
import com.oborodulin.home.common.domain.usecases.UseCase
import com.oborodulin.home.data.local.db.entities.PayerEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*

class GetPayerUseCase(
    configuration: Configuration,
    private val payersRepository: PayersRepository
) : UseCase<GetPayerUseCase.Request, GetPayerUseCase.Response>(configuration) {

    override fun process(request: Request): Flow<Response> =
        payersRepository.get(request.id).map {
            Response(it)
        }

    data class Request(val id: UUID) : UseCase.Request
    data class Response(val payer: PayerEntity) : UseCase.Response
}