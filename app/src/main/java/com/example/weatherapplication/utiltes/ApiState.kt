package com.example.weatherapplication.utiltes

import com.example.weatherapplication.data.pojo.ForecastItem
import com.example.weatherapplication.data.pojo.WeatherResponse


sealed class WeatherApiState {
    data class Success(
        val weatherResponse: WeatherResponse
    ) : WeatherApiState()
    data class Failure(val message: Throwable) : WeatherApiState()
    object Loading : WeatherApiState()
}


sealed class ForecastApiState {
    data class Success(
        val dailyForecasts: List<ForecastItem>,
        val hourlyForecasts: List<ForecastItem>
    ) : ForecastApiState()
    data class Failure(val message: Throwable) : ForecastApiState()
    object Loading : ForecastApiState()
}
