package com.oborodulin.home.common.di

import com.google.gson.Gson
import com.oborodulin.home.common.domain.usecases.UseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CommonModule {
    /**
     * https://www.codegrepper.com/code-examples/javascript/android+object+to+json+string
     */
    @Singleton
    @Provides
    fun provideJsonLogger(): Gson = Gson()

    @Singleton
    @Provides
    fun provideUseCaseConfiguration(): UseCase.Configuration = UseCase.Configuration(Dispatchers.IO)

}