package com.example.weatherapplication.data.localdatasource.localdatsource

import com.example.weatherapplication.data.localdatasource.database.AppDatabase
import com.example.weatherapplication.data.localdatasource.sharedpreferences.SharedPreferences
import com.example.weatherapplication.data.pojo.AlarmData
import com.example.weatherapplication.data.pojo.City
import com.example.weatherapplication.data.pojo.ForecastItem
import com.example.weatherapplication.data.pojo.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LocalDataSource(private val database: AppDatabase, private val sharedPreferences: SharedPreferences) :
    ILocalDataSource {

   val cityDao = database.cityDao()
   val alarmDao = database.alarmDao()
   val weatherDao = database.weatherDao()
   val forecastDao = database.forecastDao()


    override fun getAllCities(): Flow<List<City>> {
        return cityDao.getAllCities()
    }

    override suspend fun insertCity(city: City) {
        cityDao.insertCity(city)
    }

    override suspend fun deleteCity(cityName: String) {
        cityDao.deleteCity(cityName)
    }

    override fun setLan(lan : String){
        sharedPreferences.saveLanguage(lan)
    }
    override fun getLan() : String{
        return sharedPreferences.getLanguage()
    }

    override fun setSpeed(speed : String){
        sharedPreferences.saveSpeed(speed)
    }
    override fun getSpeed() : String{
        return sharedPreferences.getSpeed()
    }

    override fun setUnit(unit : String){
        sharedPreferences.saveUnit(unit)
    }
    override fun getUnit() : String{
        return sharedPreferences.getUnit()
    }

    override fun setLocation(location: String){
        sharedPreferences.saveLocation(location)
    }
    override fun getLocation(): String{
        return sharedPreferences.getLocation()
    }

    override fun setNotification(notification: String){
        sharedPreferences.saveNotification(notification)
    }
    override fun getNotification(): String{
        return sharedPreferences.getNotification()
    }

    override fun getRequestCode() : Int {
        return sharedPreferences.getRequestCode()
    }

    override fun setRequestCode(requestCode: Int){
        sharedPreferences.saveRequestCode(requestCode)
    }

    override fun getAllLocalAlarm(): Flow<List<AlarmData>> {
        return alarmDao.getAllLocalAlarm()
    }

    override suspend fun insertAlarmData(alarmData: AlarmData) {
        alarmDao.insertAlarm(alarmData)
    }

    override suspend fun deletAlarm(alarmData: AlarmData) {
        alarmDao.deleteAlarm(alarmData)
    }

    override suspend fun deleteOldAlarms(currentTimeMillis: Long) {
        alarmDao.deleteOldAlarms(currentTimeMillis)
    }

    override suspend fun insertWeather(weatherResponse: WeatherResponse) {
        weatherDao.insertWeather(weatherResponse)
    }

    override fun getLastWeather(): Flow<WeatherResponse?> = flow {
        emit(weatherDao.getLastWeather())
    }

    override suspend fun deleteAllWeather() {
        weatherDao.deleteAllWeather()
    }

    override suspend fun insertForecastItems(forecastItems: List<ForecastItem>) {
        forecastDao.insertForecastItems(forecastItems)
    }
    override suspend fun deleteAllForecastItems() {
        forecastDao.deleteAllForecastItems()
    }

    override fun getForecastItemsByType(type: String): Flow<List<ForecastItem>> {
        return forecastDao.getForecastItemsByType(type)
    }
}