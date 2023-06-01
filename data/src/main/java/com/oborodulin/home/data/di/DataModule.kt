package com.oborodulin.home.data.di

import com.oborodulin.home.common.di.IoDispatcher
import com.oborodulin.home.data.local.db.dao.AppSettingDao
import com.oborodulin.home.data.local.db.dao.PayerDao
import com.oborodulin.home.data.local.db.mappers.*
import com.oborodulin.home.data.local.db.repositories.*
import com.oborodulin.home.data.local.db.repositories.sources.local.LocalAppSettingDataSource
import com.oborodulin.home.data.local.db.repositories.sources.local.LocalPayerDataSource
import com.oborodulin.home.data.local.db.sources.local.LocalAppSettingDataSourceImpl
import com.oborodulin.home.data.local.db.sources.local.LocalPayerDataSourceImpl
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
    // AppSettings:
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
    fun provideAppSettingMappers(
        appSettingEntityListToAppSettingListMapper: AppSettingEntityListToAppSettingListMapper,
        appSettingEntityToAppSettingMapper: AppSettingEntityToAppSettingMapper,
        appSettingToAppSettingEntityMapper: AppSettingToAppSettingEntityMapper
    ): AppSettingMappers = AppSettingMappers(
        appSettingEntityListToAppSettingListMapper,
        appSettingEntityToAppSettingMapper,
        appSettingToAppSettingEntityMapper
    )

    // Payers:
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

    @Singleton
    @Provides
    fun providePayerMappers(
        payerEntityListToPayerListMapper: PayerEntityListToPayerListMapper,
        payerEntityToPayerMapper: PayerEntityToPayerMapper,
        payerToPayerEntityMapper: PayerToPayerEntityMapper
    ): PayerMappers = PayerMappers(
        payerEntityListToPayerListMapper,
        payerEntityToPayerMapper,
        payerToPayerEntityMapper
    )

    // DATA SOURCES:
    @Singleton
    @Provides
    fun provideAppSettingDataSource(
        appSettingDao: AppSettingDao, @IoDispatcher dispatcher: CoroutineDispatcher
    ): LocalAppSettingDataSource = LocalAppSettingDataSourceImpl(appSettingDao, dispatcher)

    @Singleton
    @Provides
    fun providePayerDataSource(
        payerDao: PayerDao, @IoDispatcher dispatcher: CoroutineDispatcher
    ): LocalPayerDataSource = LocalPayerDataSourceImpl(payerDao, dispatcher)

    // REPOSITORIES:
    @Singleton
    @Provides
    fun provideAppSettingsRepository(
        localAppSettingDataSource: LocalAppSettingDataSource, mappers: AppSettingMappers
    ): AppSettingsRepository = AppSettingsRepositoryImpl(localAppSettingDataSource, mappers)

    @Singleton
    @Provides
    fun providePayersRepository(localPayerDataSource: LocalPayerDataSource, mappers: PayerMappers):
            PayersRepository = PayersRepositoryImpl(localPayerDataSource, mappers)
}