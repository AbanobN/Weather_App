package com.example.weatherapplication.data.localdatasource.fakelocaldatasource

import com.example.weatherapplication.data.localdatasource.localdatsource.ILocalDataSource
import com.example.weatherapplication.data.pojo.AlarmData
import com.example.weatherapplication.data.pojo.City
import com.example.weatherapplication.data.pojo.ForecastItem
import com.example.weatherapplication.data.pojo.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeLocalDataSource : ILocalDataSource {

    // Mock Daos for testing
    private val citiesList = mutableListOf<City>()
    private val alarmsList = mutableListOf<AlarmData>()
    private val weatherList = mutableListOf<WeatherResponse>()
    private val forecastItemsList = mutableListOf<ForecastItem>()

    // Simulated shared preferences
    private var lan: String = ""
    private var speed: String = ""
    private var unit: String = ""
    private var location: String = ""
    private var notification: String = ""


    override fun getAllCities(): Flow<List<City>> = flow {
        emit(citiesList)
    }

    override suspend fun insertCity(city: City) {
        citiesList.add(city)
    }

    override suspend fun deleteCity(cityName: String) {
        citiesList.removeAll { it.name == cityName }
    }

    override fun setLan(lan: String) {
        this.lan = lan
    }

    override fun getLan(): String {
        return lan
    }

    override fun setSpeed(speed: String) {
        this.speed = speed
    }

    override fun getSpeed(): String {
        return speed
    }

    override fun setUnit(unit: String) {
        this.unit = unit
    }

    override fun getUnit(): String {
        return unit
    }

    override fun setLocation(location: String) {
        this.location = location
    }

    override fun getLocation(): String {
        return location
    }

    override fun setNotification(notification: String) {
        this.notification = notification
    }

    override fun getNotification(): String {
        return notification
    }

    override fun getAllLocalAlarm(): Flow<List<AlarmData>> = flow {
        emit(alarmsList)
    }

    override suspend fun insertAlarmData(alarmData: AlarmData) {
        alarmsList.add(alarmData)
    }

    override suspend fun deletAlarm(alarmData: AlarmData) {
        alarmsList.remove(alarmData)
    }

    override suspend fun deleteOldAlarms(currentTimeMillis: Long) {
        alarmsList.removeAll { it.time < currentTimeMillis }
    }

    override suspend fun insertWeather(weatherResponse: WeatherResponse) {
        weatherList.clear()
        weatherList.add(weatherResponse)
    }

    override fun getLastWeather(): Flow<WeatherResponse?> = flow {
        emit(weatherList.lastOrNull())
    }

    override suspend fun deleteAllWeather() {
        weatherList.clear()
    }

    override suspend fun insertForecastItems(forecastItems: List<ForecastItem>) {
        forecastItemsList.addAll(forecastItems)
    }

    override suspend fun deleteAllForecastItems() {
        forecastItemsList.clear()
    }

    override fun getForecastItemsByType(type: String): Flow<List<ForecastItem>> = flow {
        emit(forecastItemsList.filter { it.type == type })
    }
}
