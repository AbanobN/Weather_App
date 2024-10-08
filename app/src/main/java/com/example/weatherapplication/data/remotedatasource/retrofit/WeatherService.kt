package com.example.weatherapplication.data.remotedatasource.retrofit


import com.example.weatherapplication.BuildConfig
import com.example.weatherapplication.data.pojo.ForecastResponse
import com.example.weatherapplication.data.pojo.LocationResponse
import com.example.weatherapplication.data.pojo.UVResponse
import com.example.weatherapplication.data.pojo.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("data/2.5/weather")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("lang") language: String,
        @Query("appid") apiKey: String = BuildConfig.API_KEY
    ): WeatherResponse

    @GET("geo/1.0/reverse")
    suspend fun getLocationByCoordinates(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") appId: String = BuildConfig.API_KEY
    ):List<LocationResponse>

    @GET("geo/1.0/direct")
    suspend fun getLocationByName(
        @Query("q") cityName: String,
        @Query("appid") appId: String = BuildConfig.API_KEY
    ): List<LocationResponse>

    @GET("data/2.5/uvi")
    suspend fun getUVIndex(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String = BuildConfig.API_KEY
    ): UVResponse

    @GET("data/2.5/forecast")
    suspend fun getForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("lang") language: String,
        @Query("appid") apiKey: String = BuildConfig.API_KEY
    ): ForecastResponse


}
