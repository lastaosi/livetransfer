package com.jh.livetransfer.domain.usecase.weather

import com.jh.livetransfer.domain.repository.WeatherRepository
import javax.inject.Inject

class SaveCityNamesUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(cities: List<String>){
        repository.saveCityNames(cities)
    }
}