package com.example.weatherapplication.data.localdatasource.localdatsource

import com.example.weatherapplication.data.localdatasource.database.AlarmDao
import com.example.weatherapplication.data.localdatasource.database.CityDao
import com.example.weatherapplication.data.localdatasource.database.ForecastDao
import com.example.weatherapplication.data.localdatasource.database.WeatherDao
import com.example.weatherapplication.data.pojo.AlarmData
import com.example.weatherapplication.data.pojo.City
import com.example.weatherapplication.data.pojo.ForecastItem
import com.example.weatherapplication.data.pojo.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface ILocalDataSource {
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
    fun getRequestCode() : Int
    fun setRequestCode(requestCode: Int)

    suspend fun insertAlarmData(alarmData: AlarmData)

    suspend fun deletAlarm(alarmData: AlarmData)

    suspend fun deleteOldAlarms(currentTimeMillis: Long)

    suspend fun insertWeather(weatherResponse: WeatherResponse)
    fun getLastWeather(): Flow<WeatherResponse?>

    suspend fun deleteAllWeather()

    suspend fun insertForecastItems(forecastItems: List<ForecastItem>)

    suspend fun deleteAllForecastItems()
    fun getForecastItemsByType(type: String): Flow<List<ForecastItem>>
}