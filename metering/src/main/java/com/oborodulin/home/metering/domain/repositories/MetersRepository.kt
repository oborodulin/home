package com.oborodulin.home.metering.domain.repositories

import com.oborodulin.home.data.local.db.views.PrevMetersValuesView
import com.oborodulin.home.metering.domain.model.Meter
import com.oborodulin.home.metering.domain.model.MeterValue
import com.oborodulin.home.metering.domain.model.MeterVerification
import kotlinx.coroutines.flow.Flow
import java.util.*

interface MetersRepository {
    fun getAll(): Flow<List<Meter>>
    fun get(id: UUID): Flow<Meter>
    fun getMeters(payerId: UUID): Flow<List<Meter>>
    fun getMeterValues(meterId: UUID): Flow<List<MeterValue>>
    fun getMeterVerifications(meterId: UUID): Flow<List<MeterVerification>>
    fun getPrevServiceMeterValues(payerId: UUID?): Flow<List<PrevMetersValuesView>>
    fun save(meter: Meter): Flow<Meter>
    fun save(meterValue: MeterValue): Flow<MeterValue>
    fun delete(meter: Meter): Flow<Meter>
    suspend fun deleteAll()
}