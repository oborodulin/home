package com.oborodulin.home.data.di

import android.content.Context
import com.google.gson.Gson
import com.oborodulin.home.common.di.IoDispatcher
import com.oborodulin.home.data.local.db.HomeDatabase
import com.oborodulin.home.data.local.db.dao.AppSettingDao
import com.oborodulin.home.data.local.db.dao.MeterDao
import com.oborodulin.home.data.local.db.dao.PayerDao
import com.oborodulin.home.data.local.db.dao.RateDao
import com.oborodulin.home.data.local.db.dao.ReceiptDao
import com.oborodulin.home.data.local.db.dao.ServiceDao
import com.oborodulin.home.data.local.db.mappers.*
import com.oborodulin.home.data.local.db.repositories.*
import com.oborodulin.home.domain.repositories.AppSettingsRepository
import com.oborodulin.home.domain.repositories.PayersRepository
import com.oborodulin.home.domain.usecases.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DataModule::class]
)
object TestDataModule {
    //Persistence
    @Singleton
    @Provides
    fun provideHomeDatabase(
        @ApplicationContext appContext: Context, jsonLogger: Gson
    ): HomeDatabase = HomeDatabase.getTestInstance(appContext, jsonLogger)

    @Singleton
    @Provides
    fun provideAppSettingDao(db: HomeDatabase): AppSettingDao = db.appSettingDao()

    @Singleton
    @Provides
    fun providePayerDao(db: HomeDatabase): PayerDao = db.payerDao()

    @Singleton
    @Provides
    fun provideMeterDao(db: HomeDatabase): MeterDao = db.meterDao()

    @Singleton
    @Provides
    fun provideServiceDao(db: HomeDatabase): ServiceDao = db.serviceDao()

    @Singleton
    @Provides
    fun provideRateDao(db: HomeDatabase): RateDao = db.rateDao()

    @Singleton
    @Provides
    fun provideReceiptDao(db: HomeDatabase): ReceiptDao = db.receiptDao()

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