package com.oborodulin.home.metering.data.repositories.sources.local

import com.oborodulin.home.data.local.db.entities.MeterEntity
import com.oborodulin.home.data.local.db.entities.MeterTlEntity
import com.oborodulin.home.data.local.db.entities.MeterValueEntity
import com.oborodulin.home.data.local.db.entities.MeterVerificationEntity
import com.oborodulin.home.data.local.db.views.MeterValuePrevPeriodView
import com.oborodulin.home.data.local.db.views.MeterView
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface LocalMeteringDataSource {
    fun getMeters(): Flow<List<MeterView>>
    fun getMeters(payerId: UUID): Flow<List<MeterView>>
    fun getMeter(id: UUID): Flow<MeterView>
    fun getPrevServiceMeterValues(payerId: UUID?): Flow<List<MeterValuePrevPeriodView>>
    fun getMeterValues(meterId: UUID): Flow<List<MeterValueEntity>>
    fun getMeterVerifications(meterId: UUID): Flow<List<MeterVerificationEntity>>
    suspend fun insertMeter(meter: MeterEntity, textContent: MeterTlEntity)
    suspend fun updateMeter(meter: MeterEntity, textContent: MeterTlEntity)
    suspend fun insertMeterValue(meterValue: MeterValueEntity)
    suspend fun updateMeterValue(meterValue: MeterValueEntity)
    suspend fun deleteMeter(meter: MeterEntity)
    suspend fun deleteMeters(meters: List<MeterEntity>)
    suspend fun deleteMeters()
    suspend fun deleteMeterCurrentValue(meterId: UUID)
}