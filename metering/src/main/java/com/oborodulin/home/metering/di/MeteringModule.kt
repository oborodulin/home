package com.oborodulin.home.metering.di

import com.oborodulin.home.common.di.IoDispatcher
import com.oborodulin.home.common.domain.usecases.UseCase
import com.oborodulin.home.data.local.db.dao.MeterDao
import com.oborodulin.home.metering.data.mappers.*
import com.oborodulin.home.metering.data.repositories.sources.local.LocalMeteringDataSource
import com.oborodulin.home.metering.data.sources.local.LocalMeteringDataSourceImpl
import com.oborodulin.home.metering.data.repositories.MetersRepositoryImpl
import com.oborodulin.home.metering.domain.repositories.MetersRepository
import com.oborodulin.home.metering.domain.usecases.*
import com.oborodulin.home.metering.ui.model.converters.PrevServiceMeterValuesListConverter
import com.oborodulin.home.metering.ui.model.mappers.MeterValueListItemToMeterValueMapper
import com.oborodulin.home.metering.ui.model.mappers.MeterValueToMeterValueListItemMapper
import com.oborodulin.home.metering.ui.model.mappers.PrevMetersValuesViewToMeterValueListItemListMapper
import com.oborodulin.home.metering.ui.model.mappers.PrevMetersValuesViewToMeterValueListItemMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MeteringModule {
    // DATA MAPPERS:
    @Singleton
    @Provides
    fun provideMeterToMeterEntityMapper(): MeterToMeterEntityMapper = MeterToMeterEntityMapper()

    @Singleton
    @Provides
    fun provideMeterListToMeterEntityListMapper(mapper: MeterToMeterEntityMapper): MeterListToMeterEntityListMapper =
        MeterListToMeterEntityListMapper(mapper = mapper)

    @Singleton
    @Provides
    fun provideMeterToMeterTlEntityMapper(): MeterToMeterTlEntityMapper =
        MeterToMeterTlEntityMapper()

    @Singleton
    @Provides
    fun provideMetersViewToMeterMapper(): MeterViewToMeterMapper = MeterViewToMeterMapper()

    @Singleton
    @Provides
    fun provideMetersViewToMeterListMapper(mapper: MeterViewToMeterMapper): MeterViewToMeterListMapper =
        MeterViewToMeterListMapper(mapper = mapper)

    @Singleton
    @Provides
    fun provideMeterValueEntityToMeterValueMapper(): MeterValueEntityToMeterValueMapper =
        MeterValueEntityToMeterValueMapper()

    @Singleton
    @Provides
    fun provideMeterValueEntityListToMeterValueListMapper(mapper: MeterValueEntityToMeterValueMapper): MeterValueEntityListToMeterValueListMapper =
        MeterValueEntityListToMeterValueListMapper(mapper = mapper)

    @Singleton
    @Provides
    fun provideMeterValueToMeterValueEntityMapper(): MeterValueToMeterValueEntityMapper =
        MeterValueToMeterValueEntityMapper()

    @Singleton
    @Provides
    fun provideMeterVerificationEntityToMeterVerificationMapper(): MeterVerificationEntityToMeterVerificationMapper =
        MeterVerificationEntityToMeterVerificationMapper()

    @Singleton
    @Provides
    fun provideMeterVerificationEntityListToMeterVerificationListMapper(mapper: MeterVerificationEntityToMeterVerificationMapper): MeterVerificationEntityListToMeterVerificationListMapper =
        MeterVerificationEntityListToMeterVerificationListMapper(mapper = mapper)

    // UI MAPPERS:
    @Singleton
    @Provides
    fun provideMeterValueToMeterValueListItemMapper(): MeterValueToMeterValueListItemMapper =
        MeterValueToMeterValueListItemMapper()

    @Singleton
    @Provides
    fun provideMeterValueListItemToMeterValueMapper(): MeterValueListItemToMeterValueMapper =
        MeterValueListItemToMeterValueMapper()

    @Singleton
    @Provides
    fun providePrevMetersValuesViewToMeterValueListItemMapper(): PrevMetersValuesViewToMeterValueListItemMapper =
        PrevMetersValuesViewToMeterValueListItemMapper()

    @Singleton
    @Provides
    fun providePrevMetersValuesViewToMeterValueListItemListMapper(mapper: PrevMetersValuesViewToMeterValueListItemMapper): PrevMetersValuesViewToMeterValueListItemListMapper =
        PrevMetersValuesViewToMeterValueListItemListMapper(mapper = mapper)

    // CONVERTERS:
    @Singleton
    @Provides
    fun providePrevServiceMeterValuesListConverter(mapper: PrevMetersValuesViewToMeterValueListItemMapper): PrevServiceMeterValuesListConverter =
        PrevServiceMeterValuesListConverter(mapper = mapper)

    // DATA SOURCES:
    @Singleton
    @Provides
    fun provideMeteringDataSource(
        meterDao: MeterDao,
        @IoDispatcher dispatcher: CoroutineDispatcher,
        meterViewToMeterListMapper: MeterViewToMeterListMapper,
        meterViewToMeterMapper: MeterViewToMeterMapper,
        meterValueEntityListToMeterValueListMapper: MeterValueEntityListToMeterValueListMapper,
        meterVerificationEntityListToMeterVerificationListMapper: MeterVerificationEntityListToMeterVerificationListMapper,
        meterValueToMeterValueEntityMapper: MeterValueToMeterValueEntityMapper,
        meterToMeterEntityMapper: MeterToMeterEntityMapper,
        meterToMeterTlEntityMapper: MeterToMeterTlEntityMapper
    ): LocalMeteringDataSource =
        LocalMeteringDataSourceImpl(
            meterDao,
            dispatcher,
            meterViewToMeterListMapper,
            meterViewToMeterMapper,
            meterValueEntityListToMeterValueListMapper,
            meterVerificationEntityListToMeterVerificationListMapper,
            meterValueToMeterValueEntityMapper,
            meterToMeterEntityMapper,
            meterToMeterTlEntityMapper
        )

    // REPOSITORIES:
    @Singleton
    @Provides
    fun provideMetersRepository(localMeteringDataSource: LocalMeteringDataSource): MetersRepository =
        MetersRepositoryImpl(localMeteringDataSource)

    // USE CASES:
    @Singleton
    @Provides
    fun provideMeterUseCases(configuration: UseCase.Configuration, repository: MetersRepository):
            MeterUseCases =
        MeterUseCases(
            getMetersUseCase = GetMetersUseCase(configuration, repository),
            getPrevServiceMeterValuesUseCase = GetPrevServiceMeterValuesUseCase(
                configuration,
                repository
            ),
            deleteMeterValueUseCase = DeleteMeterValueUseCase(configuration, repository),
            saveMeterValueUseCase = SaveMeterValueUseCase(configuration, repository)
        )
}