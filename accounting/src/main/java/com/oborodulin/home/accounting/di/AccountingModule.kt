package com.oborodulin.home.accounting.di

import com.oborodulin.home.accounting.data.mappers.PayerEntityMapper
import com.oborodulin.home.accounting.data.repositories.AccountingDataSource
import com.oborodulin.home.accounting.data.repositories.AccountingDataSourceImpl
import com.oborodulin.home.accounting.data.repositories.PayersRepositoryImp
import com.oborodulin.home.accounting.domain.repositories.PayersRepository
import com.oborodulin.home.accounting.domain.usecases.*
import com.oborodulin.home.common.di.IoDispatcher
import com.oborodulin.home.data.local.db.dao.PayerDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
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
    fun providePayerUseCases(repository: PayersRepository): PayerUseCases =
        PayerUseCases(
            getPayer = GetPayer(repository),
            getPayers = GetPayers(repository),
            savePayer = SavePayer(repository),
            deletePayer = DeletePayer(repository)
        )
}