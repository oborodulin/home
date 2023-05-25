package com.oborodulin.home.data.di

import com.oborodulin.home.data.local.db.HomeDatabase
import com.oborodulin.home.data.local.db.dao.AppSettingDao
import com.oborodulin.home.data.local.db.dao.MeterDao
import com.oborodulin.home.data.local.db.dao.PayerDao
import com.oborodulin.home.data.local.db.dao.RateDao
import com.oborodulin.home.data.local.db.dao.ReceiptDao
import com.oborodulin.home.data.local.db.dao.ServiceDao
import com.oborodulin.home.data.local.db.mappers.*
import com.oborodulin.home.data.local.db.repositories.*
import com.oborodulin.home.domain.usecases.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {
    // PERSISTENCE
    // DAO:
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

}