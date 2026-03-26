package com.jh.livetransfer.domain.usecase.weather

import com.jh.livetransfer.data.model.WeatherResponse
import com.jh.livetransfer.domain.repository.WeatherRepository
import javax.inject.Inject

class GetWeatherByLocationUseCase @Inject constructor(
    private val repository: WeatherRepository
){
    suspend operator fun invoke(lat:Double,lon:Double) : Result<WeatherResponse>{
        return repository.getWeatherByLocation(lat,lon)
    }
}