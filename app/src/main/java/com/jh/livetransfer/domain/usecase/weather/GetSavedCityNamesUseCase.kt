package com.jh.livetransfer.domain.usecase.weather

import com.jh.livetransfer.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSavedCityNamesUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    operator fun invoke() : Flow<List<String>>{
        return repository.getSavedCityNames()
    }
}