package com.jh.livetransfer.di

import com.jh.livetransfer.domain.repository.TranslationRepository
import com.jh.livetransfer.domain.usecase.GetRealtimeTranslationUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideGetRealtimeTranslationUseCase(
        repository: TranslationRepository
    ): GetRealtimeTranslationUseCase {
        return GetRealtimeTranslationUseCase(repository)
    }
}
