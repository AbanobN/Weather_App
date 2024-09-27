package com.example.weatherapplication.data.remotedatasource.remotedatasource

import com.example.weatherapplication.data.pojo.ForecastResponse
import com.example.weatherapplication.data.pojo.LocationResponse
import com.example.weatherapplication.data.pojo.UVResponse
import com.example.weatherapplication.data.pojo.WeatherResponse
import com.example.weatherapplication.data.remotedatasource.retrofit.WeatherService

interface IRemoteDataSource {

    suspend fun getWeather(lat: Double, lon: Double, lan: String): WeatherResponse

    suspend fun getLocationByCoordinates(lat: Double, lon: Double): List<LocationResponse>

    suspend fun getLocationByCityName(cityName: String): List<LocationResponse>
    suspend fun getUVIndex(lat: Double, lon: Double): UVResponse

    suspend fun getForecast(lat: Double, lon: Double, lan: String): ForecastResponse
}