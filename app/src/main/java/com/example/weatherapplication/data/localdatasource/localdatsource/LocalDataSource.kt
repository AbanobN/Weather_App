package com.example.weatherapplication.data.localdatasource.localdatsource

import com.example.weatherapplication.data.localdatasource.database.AppDatabase
import com.example.weatherapplication.data.pojo.City
import kotlinx.coroutines.flow.Flow

class LocalDataSource(private val database: AppDatabase) {
    private val cityDao = database.cityDao()

    fun getAllCities(): Flow<List<City>> {
        return cityDao.getAllCities()
    }

    suspend fun insertCity(city: City) {
        cityDao.insertCity(city)
    }

    suspend fun deleteCity(cityName: String) {
        cityDao.deleteCity(cityName)
    }
}