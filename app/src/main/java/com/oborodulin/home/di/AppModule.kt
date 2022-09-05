package com.oborodulin.home.di

import android.content.Context
import com.google.gson.Gson
import com.oborodulin.home.data.local.db.HomeDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    /**
     * https://www.codegrepper.com/code-examples/javascript/android+object+to+json+string
     */
    @Singleton
    @Provides
    fun provideJsonLogger(): Gson = Gson()
}