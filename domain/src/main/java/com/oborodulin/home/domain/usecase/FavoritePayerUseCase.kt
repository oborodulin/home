package com.oborodulin.home.domain.usecase

import com.oborodulin.home.common.domain.usecases.UseCase
import com.oborodulin.home.domain.repositories.PayersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*

class FavoritePayerUseCase(
    configuration: Configuration,
    private val payersRepository: PayersRepository
) :
    UseCase<FavoritePayerUseCase.Request, FavoritePayerUseCase.Response>(configuration) {
    override fun process(request: Request): Flow<Response> {
        return payersRepository.favoriteById(request.payerId)
            .map {
                Response
            }
    }

    data class Request(val payerId: UUID) : UseCase.Request
    object Response : UseCase.Response
}