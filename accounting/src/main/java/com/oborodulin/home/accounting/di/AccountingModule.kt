package com.oborodulin.home.accounting.di

import com.oborodulin.home.accounting.data.mappers.PayerEntityMapper
import com.oborodulin.home.accounting.data.repositories.AccountingDataSource
import com.oborodulin.home.accounting.data.repositories.AccountingDataSourceImpl
import com.oborodulin.home.accounting.data.repositories.PayersRepositoryImp
import com.oborodulin.home.accounting.domain.converters.PayersListConverter
import com.oborodulin.home.accounting.domain.repositories.PayersRepository
import com.oborodulin.home.accounting.domain.usecases.*
import com.oborodulin.home.common.di.IoDispatcher
import com.oborodulin.home.common.domain.usecases.UseCase
import com.oborodulin.home.data.local.db.dao.PayerDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AccountingModule {

    @Singleton
    @Provides
    fun providePayerEntityMapper(): PayerEntityMapper = PayerEntityMapper()

    @Singleton
    @Provides
    fun provideAccountingDataSource(
        payerDao: PayerDao,
        @IoDispatcher dispatcher: CoroutineDispatcher,
        payerEntityMapper: PayerEntityMapper
    ): AccountingDataSource =
        AccountingDataSourceImpl(payerDao, dispatcher, payerEntityMapper)

    @Singleton
    @Provides
    fun providePayersRepository(accountingDataSource: AccountingDataSource): PayersRepository =
        PayersRepositoryImp(accountingDataSource)

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
    fun provideAccountingUseCases(configuration: UseCase.Configuration, repository: PayersRepository):
            AccountingUseCases =
        AccountingUseCases(
            getPrevServiceMeterValuesUseCase = GetPrevServiceMeterValuesUseCase(configuration, repository),
        )
}