package com.oborodulin.home.metering.data.mappers

class MeterMappers(
    val meterViewToMeterListMapper: MeterViewToMeterListMapper,
    val meterViewToMeterMapper: MeterViewToMeterMapper,
    val meterValueEntityListToMeterValueListMapper: MeterValueEntityListToMeterValueListMapper,
    val meterVerificationEntityListToMeterVerificationListMapper: MeterVerificationEntityListToMeterVerificationListMapper,
    val meterValueToMeterValueEntityMapper: MeterValueToMeterValueEntityMapper,
    val meterToMeterEntityMapper: MeterToMeterEntityMapper,
    val meterToMeterTlEntityMapper: MeterToMeterTlEntityMapper
)
/*{

    fun toMeterValueEntityList(meter: Meter) =
        meter.meterValues?.map {
            MeterValueEntity(
                meterValueId = it.id ?: it.apply { id = UUID.randomUUID() }.id!!,
                metersId = meter.id ?: meter.apply { id = UUID.randomUUID() }.id!!,
                valueDate = it.valueDate,
                meterValue = it.meterValue,
            )
        }

    fun toMeterVerificationEntityList(meter: Meter) =
        meter.meterVerifications?.map {
            MeterVerificationEntity(
                startDate = it.startDate,
                endDate = it.endDate,
                startMeterValue = it.startMeterValue,
                endMeterValue = it.endMeterValue,
                isOk = it.isOk,
                metersId = meter.id ?: meter.apply { id = UUID.randomUUID() }.id!!,
            )
        }
}
 */