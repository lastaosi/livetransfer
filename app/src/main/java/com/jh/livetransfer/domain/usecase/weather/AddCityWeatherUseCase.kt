package com.jh.livetransfer.domain.usecase.weather

import com.jh.livetransfer.data.model.WeatherResponse
import com.jh.livetransfer.domain.repository.WeatherRepository
import javax.inject.Inject

class AddCityWeatherUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(city:String) : Result<WeatherResponse>{
        return repository.getWeatherByCity(city)
    }
}