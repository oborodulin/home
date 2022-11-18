package com.oborodulin.home.data.di

import com.oborodulin.home.common.di.IoDispatcher
import com.oborodulin.home.data.local.db.dao.PayerDao
import com.oborodulin.home.data.local.db.mappers.PayerEntityMapper
import com.oborodulin.home.data.local.db.repositories.PayerDataSource
import com.oborodulin.home.data.local.db.repositories.PayerDataSourceImpl
import com.oborodulin.home.data.local.db.repositories.PayersRepositoryImp
import com.oborodulin.home.domain.repositories.PayersRepository
import com.oborodulin.home.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Singleton
    @Provides
    fun providePayerEntityMapper(): PayerEntityMapper = PayerEntityMapper()

    @Singleton
    @Provides
    fun providePayerDataSource(
        payerDao: PayerDao,
        @IoDispatcher dispatcher: CoroutineDispatcher,
        payerEntityMapper: PayerEntityMapper
    ): PayerDataSource =
        PayerDataSourceImpl(payerDao, dispatcher, payerEntityMapper)

    @Singleton
    @Provides
    fun providePayersRepository(payerDataSource: PayerDataSource): PayersRepository =
        PayersRepositoryImp(payerDataSource)
}