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
    // Database:
    @Singleton
    @Provides
    fun provideHomeDatabase(
        @ApplicationContext appContext: Context, jsonLogger: Gson
    ): HomeDatabase = HomeDatabase.getTestInstance(appContext, jsonLogger)
}