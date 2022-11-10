package com.oborodulin.home.accounting.domain.usecases

import com.oborodulin.home.accounting.domain.model.Payer
import com.oborodulin.home.accounting.domain.repositories.PayersRepository
import com.oborodulin.home.common.domain.usecases.UseCase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

class SavePayerUseCase(
    configuration: Configuration,
    private val payersRepository: PayersRepository
) : UseCase<SavePayerUseCase.Request, SavePayerUseCase.Response>(configuration) {

    override fun process(request: Request): Flow<Response> {
        GlobalScope.launch { payersRepository.save(request.payer) }
        return emptyFlow()
    }

    data class Request(val payer: Payer) : UseCase.Request
    object Response : UseCase.Response
}
