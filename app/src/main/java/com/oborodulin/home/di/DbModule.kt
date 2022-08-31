package com.oborodulin.home.di

import android.content.Context
import com.oborodulin.home.accounting.data.repositories.PayersRepositoryImp
import com.oborodulin.home.data.local.db.HomeDatabase
import com.oborodulin.home.data.local.db.dao.PayerDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DbModule {

    @Singleton
    @Provides
    fun provideHomeDatabase(@ApplicationContext appContext: Context) = HomeDatabase.getInstance(
        appContext
    )

    @Singleton
    @Provides
    fun providePayerDao(db: HomeDatabase) = db.payerDao()
}