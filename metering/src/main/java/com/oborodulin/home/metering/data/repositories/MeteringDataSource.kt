package com.oborodulin.home.metering.data.repositories

import com.oborodulin.home.data.local.db.entities.MeterEntity
import com.oborodulin.home.data.local.db.entities.MeterValueEntity
import com.oborodulin.home.data.local.db.entities.MeterVerificationEntity
import com.oborodulin.home.data.local.db.entities.pojo.MeterPojo
import com.oborodulin.home.data.local.db.entities.pojo.PrevServiceMeterValuePojo
import com.oborodulin.home.metering.domain.model.Meter
import kotlinx.coroutines.flow.Flow
import java.util.*

interface MeteringDataSource {
    fun getMeters(): Flow<List<MeterPojo>>
    fun getMeter(id: UUID): Flow<MeterPojo>
    fun getMeters(payerId: UUID): Flow<List<MeterPojo>>
    fun getPrevServiceMeterValues(payerId: UUID): Flow<List<PrevServiceMeterValuePojo>>
    fun getMeterAndValues(payerId: UUID): Flow<Map<MeterEntity, List<MeterValueEntity>>>
    fun getMeterAndVerifications(payerId: UUID): Flow<Map<MeterEntity, List<MeterVerificationEntity>>>
    suspend fun addMeter(meter: Meter)
    suspend fun updateMeter(meter: Meter)
    suspend fun saveMeter(meter: Meter)
    suspend fun deleteMeter(meter: Meter)
    suspend fun deleteMeters(meters: List<Meter>)
    suspend fun deleteMeters()
}