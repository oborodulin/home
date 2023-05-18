package com.oborodulin.home.data.di

import android.content.Context
import com.google.gson.Gson
import com.oborodulin.home.data.local.db.HomeDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [PersistenceModule::class]
)
object TestPersistenceModule {
    @Singleton
    @Provides
    fun provideHomeDatabase(@ApplicationContext appContext: Context, jsonLogger: Gson) =
        HomeDatabase.getTestInstance(appContext, jsonLogger)

    @Singleton
    @Provides
    fun providePayerDao(db: HomeDatabase) = db.payerDao()

    @Singleton
    @Provides
    fun provideMeterDao(db: HomeDatabase) = db.meterDao()

    @Singleton
    @Provides
    fun provideServiceDao(db: HomeDatabase) = db.serviceDao()

    @Singleton
    @Provides
    fun provideRateDao(db: HomeDatabase) = db.rateDao()

    @Singleton
    @Provides
    fun provideReceiptDao(db: HomeDatabase) = db.receiptDao()
}