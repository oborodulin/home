package com.oborodulin.home.di

import com.oborodulin.home.data.NextflixRepository
import com.oborodulin.home.domain.gateway.HomeGateway
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by tfakioglu on 13.December.2021
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoriesModule {

    @Provides
    @Singleton
    fun provideNextflixGateway(
        nextflixRepository: NextflixRepository
    ): HomeGateway = nextflixRepository
}