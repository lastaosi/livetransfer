package com.jh.livetransfer.data.repository

import com.jh.livetransfer.data.model.WeatherResponse
import com.jh.livetransfer.data.remote.KTorClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.parameters
import javax.inject.Inject

class WeatherRepository @Inject constructor(){
    private val client = KTorClient.client
    private val apiKey = "08ab4d8c18ea8338110a76f133ccd9a7"
    private val baseUrl = "https://api.openweathermap.org/data/2.5"

    //도시명으로 날씨 조회
    suspend fun getWeatherByCity(city : String) : Result<WeatherResponse>{
        return try{
            val response = client.get("$baseUrl/weather"){
                parameter("q",city)
                parameter("appid",apiKey)
                parameter("units","metric") //섭씨
                parameter("lang","kr")
            }
            Result.success(response.body<WeatherResponse>())

        }catch (e: Exception){
            Result.failure(e)
        }
    }

    // 위도 경도로 날씨 조회(현재위치용)
    suspend fun getWeatherByLocation(lat:Double,lon:Double) : Result<WeatherResponse>{
        return try{
            val response = client.get("$baseUrl/weather") {
                parameter("lat", lat)
                parameter("lon", lon)
                parameter("appid", apiKey)
                parameter("units","metric")
            }
            Result.success(response.body<WeatherResponse>())
        }catch (e:Exception){
            Result.failure(e)
        }
    }
}