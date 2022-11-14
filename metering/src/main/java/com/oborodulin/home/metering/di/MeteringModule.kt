package com.oborodulin.home.metering.di

import com.oborodulin.home.accounting.data.mappers.PayerEntityMapper
import com.oborodulin.home.accounting.data.repositories.AccountingDataSource
import com.oborodulin.home.accounting.data.repositories.AccountingDataSourceImpl
import com.oborodulin.home.accounting.data.repositories.PayersRepositoryImp
import com.oborodulin.home.accounting.domain.converters.PayersListConverter
import com.oborodulin.home.accounting.domain.repositories.PayersRepository
import com.oborodulin.home.accounting.domain.usecases.*
import com.oborodulin.home.common.di.IoDispatcher
import com.oborodulin.home.common.domain.usecases.UseCase
import com.oborodulin.home.data.local.db.dao.MeterDao
import com.oborodulin.home.data.local.db.dao.PayerDao
import com.oborodulin.home.metering.data.mappers.MeterPojoMapper
import com.oborodulin.home.metering.data.repositories.MeteringDataSource
import com.oborodulin.home.metering.data.repositories.MeteringDataSourceImpl
import com.oborodulin.home.metering.data.repositories.MetersRepositoryImp
import com.oborodulin.home.metering.domain.repositories.MetersRepository
import com.oborodulin.home.metering.domain.usecases.MeterUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MeteringModule {

    @Singleton
    @Provides
    fun provideMeterPojoMapper(): MeterPojoMapper = MeterPojoMapper()

    @Singleton
    @Provides
    fun provideMeteringDataSource(
        meterDao: MeterDao,
        @IoDispatcher dispatcher: CoroutineDispatcher,
        meterPojoMapper: MeterPojoMapper
    ): MeteringDataSource =
        MeteringDataSourceImpl(meterDao, dispatcher, MeterPojoMapper)

    @Singleton
    @Provides
    fun provideMetersRepository(meteringDataSource: MeteringDataSource): MetersRepository =
        MetersRepositoryImp(meteringDataSource)

    @Singleton
    @Provides
    fun providePayersListConverter(mapper: MeterPojoMapper): PayersListConverter =
        PayersListConverter(mapper)

    @Provides
    fun provideUseCaseConfiguration(): UseCase.Configuration = UseCase.Configuration(Dispatchers.IO)

    @Singleton
    @Provides
    fun provideMeterUseCases(configuration: UseCase.Configuration, repository: PayersRepository):
            MeterUseCases =
        MeterUseCases(
            getPayerUseCase = GetPayerUseCase(configuration, repository),
            getPayersUseCase = GetPayersUseCase(configuration, repository),
            savePayerUseCase = SavePayerUseCase(configuration, repository),
            deletePayerUseCase = DeletePayerUseCase(configuration, repository)
        )
}