package com.oborodulin.home.metering.di

import com.oborodulin.home.common.di.IoDispatcher
import com.oborodulin.home.common.domain.usecases.UseCase
import com.oborodulin.home.data.local.db.dao.MeterDao
import com.oborodulin.home.metering.data.mappers.MeterMapper
import com.oborodulin.home.metering.data.repositories.MeteringDataSource
import com.oborodulin.home.metering.data.repositories.MeteringDataSourceImpl
import com.oborodulin.home.metering.data.repositories.MetersRepositoryImp
import com.oborodulin.home.metering.domain.repositories.MetersRepository
import com.oborodulin.home.metering.domain.usecases.GetMetersUseCase
import com.oborodulin.home.metering.domain.usecases.GetPrevServiceMeterValuesUseCase
import com.oborodulin.home.metering.domain.usecases.MeterUseCases
import com.oborodulin.home.metering.domain.usecases.SaveMeterValueUseCase
import com.oborodulin.home.metering.ui.model.converters.MeterValueConverter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MeteringModule {

    @Singleton
    @Provides
    fun provideMeterMapper(): MeterMapper = MeterMapper()

    @Singleton
    @Provides
    fun provideMeterValueConverter(): MeterValueConverter = MeterValueConverter()

    @Singleton
    @Provides
    fun provideMeteringDataSource(
        meterDao: MeterDao,
        @IoDispatcher dispatcher: CoroutineDispatcher,
        meterMapper: MeterMapper
    ): MeteringDataSource =
        MeteringDataSourceImpl(meterDao, dispatcher, meterMapper)

    @Singleton
    @Provides
    fun provideMetersRepository(meteringDataSource: MeteringDataSource): MetersRepository =
        MetersRepositoryImp(meteringDataSource)

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
            saveMeterValueUseCase = SaveMeterValueUseCase(configuration, repository)
        )
}