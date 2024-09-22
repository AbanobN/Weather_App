package com.example.weatherapplication.data.remotedatasource.remotedatasource


import com.example.weatherapplication.data.pojo.ForecastResponse
import com.example.weatherapplication.data.pojo.LocationResponse
import com.example.weatherapplication.data.pojo.UVResponse
import com.example.weatherapplication.data.pojo.WeatherResponse
import com.example.weatherapplication.data.remotedatasource.retrofit.RetrofitInstance
import retrofit2.Response

class RemoteDataSource {

    private val weatherService = RetrofitInstance.weatherService

    suspend fun getWeather(lat: Double, lon: Double, apiKey: String): Response<WeatherResponse> {
        return weatherService.getWeather(lat, lon, apiKey)
    }

    suspend fun getLocationByCoordinates(lat: Double,lon: Double, apiKey: String): Response<List<LocationResponse>>{
        return weatherService.getLocationByCoordinates(lat,lon,apiKey)
    }

    suspend fun getUVIndex(lat: Double, lon: Double, apiKey: String): Response<UVResponse> {
        return weatherService.getUVIndex(lat, lon, apiKey)
    }

    suspend fun getForecast(lat: Double, lon: Double, apiKey: String): Response<ForecastResponse> {
        return weatherService.getForecast(lat, lon, apiKey)
    }
}