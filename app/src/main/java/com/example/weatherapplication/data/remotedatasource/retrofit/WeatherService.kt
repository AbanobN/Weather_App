package com.example.weather_app_demo.data.retrofit

import com.example.weather_app_demo.data.pojo.ForecastResponse
import com.example.weather_app_demo.data.pojo.UVResponse
import com.example.weather_app_demo.data.pojo.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("weather")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String
    ): Response<WeatherResponse>

    @GET("forecast")
    suspend fun getForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String
    ): Response<ForecastResponse>

    @GET("uvi")
    suspend fun getUVIndex(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String
    ): Response<UVResponse>
}
