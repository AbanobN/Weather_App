package com.example.weatherapplication.data.localdatasource.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapplication.data.pojo.City

@Dao
interface CityDao {

    @Query("SELECT * FROM FavoritesCity")
    suspend fun getAllCities(): List<City>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCity(city: City)

    @Query("DELETE FROM FavoritesCity WHERE name = :cityName")
    suspend fun deleteCity(cityName: String)

}