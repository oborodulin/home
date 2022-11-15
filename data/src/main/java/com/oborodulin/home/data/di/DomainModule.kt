package com.oborodulin.home.data.di

import com.oborodulin.home.data.local.db.mappers.PayerEntityMapper
import com.oborodulin.home.data.local.db.repositories.PayerDataSource
import com.oborodulin.home.data.local.db.repositories.PayerDataSourceImpl
import com.oborodulin.home.data.local.db.repositories.PayersRepositoryImp
import com.oborodulin.home.accounting.ui.model.converters.PayersListConverter
import com.oborodulin.home.domain.repositories.PayersRepository
import com.oborodulin.home.accounting.domain.usecases.*
import com.oborodulin.home.common.di.IoDispatcher
import com.oborodulin.home.common.domain.usecases.UseCase
import com.oborodulin.home.data.local.db.dao.PayerDao
import com.oborodulin.home.domain.usecase.*
import com.oborodulin.home.metering.domain.repositories.MetersRepository
import com.oborodulin.home.metering.domain.usecases.GetPrevServiceMeterValuesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {

    @Singleton
    @Provides
    fun providePayerEntityMapper(): PayerEntityMapper = PayerEntityMapper()

    @Singleton
    @Provides
    fun provideAccountingDataSource(
        payerDao: PayerDao,
        @IoDispatcher dispatcher: CoroutineDispatcher,
        payerEntityMapper: PayerEntityMapper
    ): PayerDataSource =
        PayerDataSourceImpl(payerDao, dispatcher, payerEntityMapper)

    @Singleton
    @Provides
    fun providePayersRepository(payerDataSource: PayerDataSource): PayersRepository =
        PayersRepositoryImp(payerDataSource)

    @Singleton
    @Provides
    fun providePayersListConverter(mapper: PayerEntityMapper): PayersListConverter =
        PayersListConverter(mapper)

    @Provides
    fun provideUseCaseConfiguration(): UseCase.Configuration = UseCase.Configuration(Dispatchers.IO)

    @Singleton
    @Provides
    fun providePayerUseCases(configuration: UseCase.Configuration, repository: PayersRepository):
            PayerUseCases =
        PayerUseCases(
            getPayerUseCase = GetPayerUseCase(configuration, repository),
            getPayersUseCase = GetPayersUseCase(configuration, repository),
            savePayerUseCase = SavePayerUseCase(configuration, repository),
            deletePayerUseCase = DeletePayerUseCase(configuration, repository)
        )

    @Singleton
    @Provides
    fun provideAccountingUseCases(
        configuration: UseCase.Configuration,
        payersRepository: PayersRepository,
        metersRepository: MetersRepository
    ):
            AccountingUseCases =
        AccountingUseCases(
            getPrevServiceMeterValuesUseCase = GetPrevServiceMeterValuesUseCase(
                configuration,
                metersRepository
            ),
        )
}