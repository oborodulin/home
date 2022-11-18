package com.oborodulin.home.metering.data.repositories

import com.oborodulin.home.data.local.db.entities.pojo.PrevServiceMeterValuePojo
import com.oborodulin.home.metering.domain.model.Meter
import com.oborodulin.home.metering.domain.model.MeterValue
import com.oborodulin.home.metering.domain.model.MeterVerification
import kotlinx.coroutines.flow.Flow
import java.util.*

interface MeteringDataSource {
    fun getMeters(): Flow<List<Meter>>
    fun getMeters(payerId: UUID): Flow<List<Meter>>
    fun getMeter(id: UUID): Flow<Meter>
    fun getPrevServiceMeterValues(payerId: UUID): Flow<List<PrevServiceMeterValuePojo>>
    fun getMeterValues(meterId: UUID): Flow<List<MeterValue>>
    fun getMeterVerifications(meterId: UUID): Flow<List<MeterVerification>>
    suspend fun saveMeter(meter: Meter)
    suspend fun deleteMeter(meter: Meter)
    suspend fun deleteMeters(meters: List<Meter>)
    suspend fun deleteMeters()
}