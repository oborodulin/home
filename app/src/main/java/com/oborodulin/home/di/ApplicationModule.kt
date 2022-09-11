package com.oborodulin.home.di

import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {
    /**
     * https://www.codegrepper.com/code-examples/javascript/android+object+to+json+string
     */
    @Singleton
    @Provides
    fun provideJsonLogger(): Gson = Gson()
}