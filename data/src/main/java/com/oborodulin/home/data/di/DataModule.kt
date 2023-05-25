package com.oborodulin.home.data.di

import com.oborodulin.home.common.di.IoDispatcher
import com.oborodulin.home.data.local.db.dao.AppSettingDao
import com.oborodulin.home.data.local.db.dao.PayerDao
import com.oborodulin.home.data.local.db.mappers.*
import com.oborodulin.home.data.local.db.repositories.*
import com.oborodulin.home.domain.repositories.AppSettingsRepository
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
    fun provideAppSettingEntityToAppSettingMapper(): AppSettingEntityToAppSettingMapper =
        AppSettingEntityToAppSettingMapper()

    @Singleton
    @Provides
    fun provideAppSettingToAppSettingEntityMapper(): AppSettingToAppSettingEntityMapper =
        AppSettingToAppSettingEntityMapper()

    @Singleton
    @Provides
    fun provideAppSettingEntityListToAppSettingListMapper(mapper: AppSettingEntityToAppSettingMapper): AppSettingEntityListToAppSettingListMapper =
        AppSettingEntityListToAppSettingListMapper(mapper = mapper)

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
    fun provideAppSettingDataSource(
        appSettingDao: AppSettingDao,
        @IoDispatcher dispatcher: CoroutineDispatcher,
        appSettingEntityListToAppSettingListMapper: AppSettingEntityListToAppSettingListMapper,
        appSettingEntityToAppSettingMapper: AppSettingEntityToAppSettingMapper,
        appSettingToAppSettingEntityMapper: AppSettingToAppSettingEntityMapper
    ): AppSettingDataSource =
        AppSettingDataSourceImp(
            appSettingDao, dispatcher,
            appSettingEntityListToAppSettingListMapper,
            appSettingEntityToAppSettingMapper,
            appSettingToAppSettingEntityMapper
        )

    @Singleton
    @Provides
    fun providePayerDataSource(
        payerDao: PayerDao, @IoDispatcher dispatcher: CoroutineDispatcher
    ): PayerDataSource = PayerDataSourceImp(payerDao, dispatcher)

    // REPOSITORIES:
    @Singleton
    @Provides
    fun provideAppSettingsRepository(appSettingDataSource: AppSettingDataSource): AppSettingsRepository =
        AppSettingsRepositoryImp(appSettingDataSource)

    @Singleton
    @Provides
    fun providePayersRepository(
        payerDataSource: PayerDataSource,
        payerEntityListToPayerListMapper: PayerEntityListToPayerListMapper,
        payerEntityToPayerMapper: PayerEntityToPayerMapper,
        payerToPayerEntityMapper: PayerToPayerEntityMapper
    ): PayersRepository =
        PayersRepositoryImp(
            payerDataSource,
            payerEntityListToPayerListMapper,
            payerEntityToPayerMapper,
            payerToPayerEntityMapper
        )
}