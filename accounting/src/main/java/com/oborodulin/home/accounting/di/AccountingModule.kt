package com.oborodulin.home.accounting.di

import com.oborodulin.home.accounting.data.mappers.PayerEntityMapper
import com.oborodulin.home.accounting.data.repositories.AccountingDataSource
import com.oborodulin.home.accounting.data.repositories.AccountingDataSourceImpl
import com.oborodulin.home.accounting.data.repositories.PayersRepositoryImp
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
    fun providePayerEntityMapper() = PayerEntityMapper()

    @Singleton
    @Provides
    fun provideAccountingDataSource(
        payerDao: PayerDao,
        dispatcher: CoroutineDispatcher,
        payerEntityMapper: PayerEntityMapper
    ) =
        AccountingDataSourceImpl(payerDao, dispatcher, payerEntityMapper)

    @Singleton
    @Provides
    fun providePayerRepository(accountingDataSource: AccountingDataSource) =
        PayersRepositoryImp(accountingDataSource)
}