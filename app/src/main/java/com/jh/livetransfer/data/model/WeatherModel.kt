package com.jh.livetransfer.data.model

import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponse(
    val name : String,
    val main: Main,
    val weather : List<Weather>,
    val wind: Wind
)

@Serializable
data class Main(
    val temp : Double,
    val feels_like: Double,
    val humidity : Int
)

@Serializable
data class Weather(
    val description : String,
    val icon : String
)

@Serializable
data class Wind(
    val speed : Double
)

