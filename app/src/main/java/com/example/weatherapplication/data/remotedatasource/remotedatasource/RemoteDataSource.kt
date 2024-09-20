package com.example.weatherapplication.data.remotedatasource.remotedatasource

import com.example.weather_app_demo.data.pojo.ForecastResponse
import com.example.weather_app_demo.data.pojo.UVResponse
import com.example.weather_app_demo.data.pojo.WeatherResponse
import com.example.weather_app_demo.data.retrofit.RetrofitInstance
import retrofit2.Response

class RemoteDataSource {

    private val weatherService = RetrofitInstance.weatherService

    suspend fun getWeather(lat: Double, lon: Double, apiKey: String): Response<WeatherResponse> {
        return weatherService.getWeather(lat, lon, apiKey)
    }

    suspend fun getUVIndex(lat: Double, lon: Double, apiKey: String): Response<UVResponse> {
        return weatherService.getUVIndex(lat, lon, apiKey)
    }

    suspend fun getForecast(lat: Double, lon: Double, apiKey: String): Response<ForecastResponse> {
        return weatherService.getForecast(lat, lon, apiKey)
    }
}