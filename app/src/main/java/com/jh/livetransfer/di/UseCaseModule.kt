package com.jh.livetransfer.di

import com.jh.livetransfer.domain.repository.TranslationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * UseCase DI 모듈.
 * 현재 [GetRealtimeTranslationUseCase]는 내부 invoke가 주석 처리된 stub 상태.
 * ViewModel이 Repository를 직접 사용하므로 실질적으로 미사용.
 */
@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule
