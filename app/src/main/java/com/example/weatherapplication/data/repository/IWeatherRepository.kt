package com.example.weatherapplication.data.repository

import com.example.weatherapplication.data.pojo.AlarmData
import com.example.weatherapplication.data.pojo.City
import com.example.weatherapplication.data.pojo.ForecastItem
import com.example.weatherapplication.data.pojo.LocationResponse
import com.example.weatherapplication.data.pojo.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface IWeatherRepository{

    fun fetchWeather(
        lat: Double,
        lon: Double,
    ): Flow<WeatherResponse>

    fun fetchWeatherAndUpdate(
        lat: Double,
        lon: Double,
        isNetwork: Boolean
    ): Flow<WeatherResponse>

    fun fetchForecast(
        lat: Double,
        lon: Double,
    ): Flow<Pair<List<ForecastItem>, List<ForecastItem>>>

    fun fetchForecastAndUpdate(
        lat: Double,
        lon: Double,
        isNetwork: Boolean
    ): Flow<Pair<List<ForecastItem>, List<ForecastItem>>>

    fun getLocationByCoordinates(lat: Double, lon: Double): Flow<LocationResponse>
    fun getLocationByCityName(cityName: String): Flow<LocationResponse>
    fun getAllCities(): Flow<List<City>>

    suspend fun insertCity(city: City)

    suspend fun deleteCity(cityName: String)
    fun setLan(lan: String)
    fun getLan(): String
    fun setSpeed(speed: String)
    fun getSpeed(): String
    fun setUnit(unit: String)
    fun getUnit(): String
    fun setLocation(location: String)
    fun getLocation(): String
    fun setNotification(notification: String)
    fun getNotification(): String
    fun getAllLocalAlarm(): Flow<List<AlarmData>>

    suspend fun insertAlarmData(alarmData: AlarmData)

    suspend fun deletAlarm(alarmData: AlarmData)

    suspend fun deleteOldAlarms(currentTimeMillis: Long)
}