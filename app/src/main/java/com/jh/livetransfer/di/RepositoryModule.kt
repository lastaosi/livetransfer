package com.jh.livetransfer.di

import com.jh.livetransfer.data.model.Weather
import com.jh.livetransfer.data.repository.ExchangeRepositoryImpl
import com.jh.livetransfer.data.repository.TranslationRepositoryImpl
import com.jh.livetransfer.data.repository.WeatherRepositoryImpl
import com.jh.livetransfer.domain.repository.ExchangeRepository
import com.jh.livetransfer.domain.repository.TranslationRepository
import com.jh.livetransfer.domain.repository.WeatherRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Repository 바인딩 모듈.
 * @Binds를 사용해 인터페이스(TranslationRepository) → 구현체(TranslationRepositoryImpl) 매핑.
 * @Provides 대신 @Binds를 쓰는 이유: 구현체 인스턴스 생성을 Hilt에 위임해 보일러플레이트 제거.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTranslationRepository(
        translationRepositoryImpl: TranslationRepositoryImpl
    ): TranslationRepository

    @Binds
    @Singleton
    abstract fun bindWeatherRepository(
        impl : WeatherRepositoryImpl
    ) : WeatherRepository

    @Binds
    @Singleton
    abstract fun bindExchangeRepository(
        impl : ExchangeRepositoryImpl
    ) : ExchangeRepository
}
