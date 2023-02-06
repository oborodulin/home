package com.oborodulin.home.metering.di

import com.oborodulin.home.common.di.IoDispatcher
import com.oborodulin.home.common.domain.usecases.UseCase
import com.oborodulin.home.data.local.db.dao.MeterDao
import com.oborodulin.home.metering.data.mappers.*
import com.oborodulin.home.metering.data.repositories.MeteringDataSource
import com.oborodulin.home.metering.data.repositories.MeteringDataSourceImpl
import com.oborodulin.home.metering.data.repositories.MetersRepositoryImp
import com.oborodulin.home.metering.domain.repositories.MetersRepository
import com.oborodulin.home.metering.domain.usecases.*
import com.oborodulin.home.metering.ui.model.converters.PrevServiceMeterValuesListConverter
import com.oborodulin.home.metering.ui.model.mappers.MeterValueListItemModelToMeterValueMapper
import com.oborodulin.home.metering.ui.model.mappers.MeterValueToMeterValueListItemModelMapper
import com.oborodulin.home.metering.ui.model.mappers.PrevMetersValuesViewToMeterValueListItemModelListMapper
import com.oborodulin.home.metering.ui.model.mappers.PrevMetersValuesViewToMeterValueModelMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MeteringModule {
    // MAPPERS:
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
    fun provideMetersViewToMeterMapper(): MetersViewToMeterMapper = MetersViewToMeterMapper()

    @Singleton
    @Provides
    fun provideMetersViewToMeterListMapper(mapper: MetersViewToMeterMapper): MetersViewToMeterListMapper =
        MetersViewToMeterListMapper(mapper = mapper)

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


    @Singleton
    @Provides
    fun provideMeterValueToMeterValueListItemModelMapper(): MeterValueToMeterValueListItemModelMapper =
        MeterValueToMeterValueListItemModelMapper()

    @Singleton
    @Provides
    fun provideMeterValueListItemModelToMeterValueMapper(): MeterValueListItemModelToMeterValueMapper =
        MeterValueListItemModelToMeterValueMapper()

    @Singleton
    @Provides
    fun providePrevMetersValuesViewToMeterValueModelMapper(): PrevMetersValuesViewToMeterValueModelMapper =
        PrevMetersValuesViewToMeterValueModelMapper()

    @Singleton
    @Provides
    fun providePrevMetersValuesViewToMeterValueListItemModelListMapper(mapper: PrevMetersValuesViewToMeterValueModelMapper): PrevMetersValuesViewToMeterValueListItemModelListMapper =
        PrevMetersValuesViewToMeterValueListItemModelListMapper(mapper = mapper)

    // CONVERTERS:
    @Singleton
    @Provides
    fun providePrevServiceMeterValuesListConverter(mapper: PrevMetersValuesViewToMeterValueModelMapper): PrevServiceMeterValuesListConverter =
        PrevServiceMeterValuesListConverter(mapper = mapper)

    // DATA SOURCES:
    @Singleton
    @Provides
    fun provideMeteringDataSource(
        meterDao: MeterDao,
        @IoDispatcher dispatcher: CoroutineDispatcher,
        metersViewToMeterListMapper: MetersViewToMeterListMapper,
        metersViewToMeterMapper: MetersViewToMeterMapper,
        meterValueEntityListToMeterValueListMapper: MeterValueEntityListToMeterValueListMapper,
        meterVerificationEntityListToMeterVerificationListMapper: MeterVerificationEntityListToMeterVerificationListMapper,
        meterValueToMeterValueEntityMapper: MeterValueToMeterValueEntityMapper,
        meterToMeterEntityMapper: MeterToMeterEntityMapper,
        meterToMeterTlEntityMapper: MeterToMeterTlEntityMapper
    ): MeteringDataSource =
        MeteringDataSourceImpl(
            meterDao,
            dispatcher,
            metersViewToMeterListMapper,
            metersViewToMeterMapper,
            meterValueEntityListToMeterValueListMapper,
            meterVerificationEntityListToMeterVerificationListMapper,
            meterValueToMeterValueEntityMapper,
            meterToMeterEntityMapper,
            meterToMeterTlEntityMapper
        )

    // REPOSITORIES:
    @Singleton
    @Provides
    fun provideMetersRepository(meteringDataSource: MeteringDataSource): MetersRepository =
        MetersRepositoryImp(meteringDataSource)

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