package com.oborodulin.home.metering.domain.repositories

import com.oborodulin.home.data.local.db.entities.MeterEntity
import com.oborodulin.home.data.local.db.entities.MeterValueEntity
import com.oborodulin.home.data.local.db.entities.MeterVerificationEntity
import com.oborodulin.home.data.local.db.entities.pojo.MeterPojo
import com.oborodulin.home.data.local.db.entities.pojo.PrevServiceMeterValuePojo
import com.oborodulin.home.metering.domain.model.Meter
import kotlinx.coroutines.flow.Flow
import java.util.*

interface MetersRepository {
    fun getAll(): Flow<List<Meter>>
    fun get(id: UUID): Flow<Meter>
    fun getMeters(payerId: UUID): Flow<List<Meter>>
    fun getMeterAndValues(payerId: UUID): Flow<Map<MeterEntity, List<MeterValueEntity>>>
    fun getMeterAndVerifications(payerId: UUID): Flow<Map<MeterEntity, List<MeterVerificationEntity>>>
    fun getPrevServiceMeterValues(payerId: UUID): Flow<List<PrevServiceMeterValuePojo>>
    fun add(meter: Meter): Flow<Meter>
    fun update(meter: Meter): Flow<Meter>
    fun save(meter: Meter): Flow<Meter>
    fun delete(meter: Meter): Flow<Meter>
    suspend fun deleteAll()
}