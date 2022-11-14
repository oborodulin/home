package com.oborodulin.home.metering.domain.usecases

import com.oborodulin.home.common.domain.usecases.UseCase
import com.oborodulin.home.data.local.db.entities.pojo.MeterPojo
import com.oborodulin.home.metering.domain.model.Meter
import com.oborodulin.home.metering.domain.model.MeterTl
import com.oborodulin.home.metering.domain.model.MeterValue
import com.oborodulin.home.metering.domain.repositories.MetersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.util.*

class GetMetersUseCase(
    configuration: Configuration,
    private val metersRepository: MetersRepository
) : UseCase<GetMetersUseCase.Request, GetMetersUseCase.Response>(configuration) {

    override fun process(request: Request): Flow<Response> =
        combine(
            metersRepository.getMeters(request.payerId),
            metersRepository.getMeterAndValues(request.payerId),
            metersRepository.getMeterAndVerifications(request.payerId)
        ) { meterPojos, meterValues, meterVerifications ->
            val meters = meterPojos.map { meterPojo ->
                val meterTl =
                    MeterTl(measureUnit = meterPojo.measureUnit!!, descr = meterPojo.descr)
                meterTl.id = meterPojo.metersTlId
                val meterVals = meterValues.filter {
                    it.key.id == meterPojo.id
                }
                val meterVers = meterVerifications.filter {
                    it.key.id == meterPojo.id
                }
                Meter(
                    num = meterPojo.num,
                    maxValue = meterPojo.maxValue,
                    passportDate = meterPojo.passportDate,
                    verificationPeriod = meterPojo.verificationPeriod,
                    tl = meterTl,
                    meterValues = meterVals.,
                    meterVerifications = meterVers.values
                )
            }
            Response(meters)
        }
            .map {
                Response(it)
            }

    data class Request(val payerId: UUID, val serviceId: UUID) : UseCase.Request
    data class Response(val meters: List<Meter>) : UseCase.Response
}