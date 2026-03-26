package com.jh.livetransfer.domain.repository

import com.jh.livetransfer.data.model.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    suspend fun getWeatherByCity(city: String): Result<WeatherResponse>
    suspend fun getWeatherByLocation(lat: Double, lon: Double): Result<WeatherResponse>

    fun getSavedCityNames(): Flow<List<String>>
    suspend fun saveCityNames(cities: List<String>)
}