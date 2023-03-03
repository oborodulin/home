package com.oborodulin.home.data.di

import com.oborodulin.home.common.di.IoDispatcher
import com.oborodulin.home.data.local.db.dao.PayerDao
import com.oborodulin.home.data.local.db.mappers.PayerEntityListToPayerListMapper
import com.oborodulin.home.data.local.db.mappers.PayerEntityToPayerMapper
import com.oborodulin.home.data.local.db.mappers.PayerToPayerEntityMapper
import com.oborodulin.home.data.local.db.repositories.PayerDataSource
import com.oborodulin.home.data.local.db.repositories.PayerDataSourceImp
import com.oborodulin.home.data.local.db.repositories.PayersRepositoryImp
import com.oborodulin.home.domain.repositories.PayersRepository
import com.oborodulin.home.domain.usecases.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    // MAPPERS:
    @Singleton
    @Provides
    fun providePayerToPayerEntityMapper(): PayerToPayerEntityMapper = PayerToPayerEntityMapper()

    @Singleton
    @Provides
    fun providePayerEntityToPayerMapper(): PayerEntityToPayerMapper = PayerEntityToPayerMapper()

    @Singleton
    @Provides
    fun providePayerEntityListToPayerListMapper(mapper: PayerEntityToPayerMapper): PayerEntityListToPayerListMapper =
        PayerEntityListToPayerListMapper(mapper = mapper)

    // DATA SOURCES:
    @Singleton
    @Provides
    fun providePayerDataSource(
        payerDao: PayerDao,
        @IoDispatcher dispatcher: CoroutineDispatcher,
        payerEntityListToPayerListMapper: PayerEntityListToPayerListMapper,
        payerEntityToPayerMapper: PayerEntityToPayerMapper,
        payerToPayerEntityMapper: PayerToPayerEntityMapper
    ): PayerDataSource =
        PayerDataSourceImp(
            payerDao, dispatcher,
            payerEntityListToPayerListMapper,
            payerEntityToPayerMapper,
            payerToPayerEntityMapper
        )

    // REPOSITORIES:
    @Singleton
    @Provides
    fun providePayersRepository(payerDataSource: PayerDataSource): PayersRepository =
        PayersRepositoryImp(payerDataSource)
}