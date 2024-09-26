package com.example.weatherapplication.data.localdatasource.localdatsource

import com.example.weatherapplication.data.localdatasource.database.AppDatabase
import com.example.weatherapplication.data.localdatasource.sharedpreferences.SharedPreferences
import com.example.weatherapplication.data.pojo.AlarmData
import com.example.weatherapplication.data.pojo.City
import kotlinx.coroutines.flow.Flow

class LocalDataSource(private val database: AppDatabase, private val sharedPreferences: SharedPreferences) {
    private val cityDao = database.cityDao()
    private val alarmDao = database.alarmDao()


    fun getAllCities(): Flow<List<City>> {
        return cityDao.getAllCities()
    }

    suspend fun insertCity(city: City) {
        cityDao.insertCity(city)
    }

    suspend fun deleteCity(cityName: String) {
        cityDao.deleteCity(cityName)
    }

    fun setLan(lan : String){
        sharedPreferences.saveLanguage(lan)
    }
    fun getLan() : String{
        return sharedPreferences.getLanguage()
    }

    fun setSpeed(speed : String){
        sharedPreferences.saveSpeed(speed)
    }
    fun getSpeed() : String{
        return sharedPreferences.getSpeed()
    }

    fun setUnit(unit : String){
        sharedPreferences.saveUnit(unit)
    }
    fun getUnit() : String{
        return sharedPreferences.getUnit()
    }

    fun setLocation(location: String){
        sharedPreferences.saveLocation(location)
    }
    fun getLocation(): String{
        return sharedPreferences.getLocation()
    }

    fun setNotification(notification: String){
        sharedPreferences.saveNotification(notification)
    }
    fun getNotification(): String{
        return sharedPreferences.getNotification()
    }

    fun getAllLocalAlarm(): Flow<List<AlarmData>> {
        return alarmDao.getAllLocalAlarm()
    }

    suspend fun insertAlarmData(alarmData: AlarmData) {
        alarmDao.insertAlarm(alarmData)
    }

    suspend fun deletAlarm(alarmData: AlarmData) {
        alarmDao.deleteAlarm(alarmData)
    }

    suspend fun deleteOldAlarms(currentTimeMillis: Long) {
        alarmDao.deleteOldAlarms(currentTimeMillis)
    }
}