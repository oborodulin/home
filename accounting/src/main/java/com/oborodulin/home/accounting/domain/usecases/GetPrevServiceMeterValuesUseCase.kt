package com.oborodulin.home.accounting.domain.usecases

import com.oborodulin.home.accounting.domain.repositories.PayersRepository
import com.oborodulin.home.common.domain.usecases.UseCase
import com.oborodulin.home.data.local.db.entities.pojo.PrevServiceMeterValuePojo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetPrevServiceMeterValuesUseCase {
    configuration: Configuration,
    private val payersRepository: PayersRepository
    ) : UseCase<GetPrevServiceMeterValuesUseCase.Request, GetPrevServiceMeterValuesUseCase.Response>(configuration) {

        override fun process(request: Request): Flow<Response> =
            payersRepository.getAll().map {
                Response(it)
            }

        object Request : UseCase.Request
        data class Response(val prevServiceMeterVals: List<PrevServiceMeterValuePojo>) : UseCase.Response
    }