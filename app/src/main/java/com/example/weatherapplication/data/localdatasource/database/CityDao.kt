package com.example.weatherapplication.data.localdatasource.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapplication.data.pojo.City
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDao {

    @Query("SELECT * FROM FavoritesCity")
    fun getAllCities(): Flow<List<City>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCity(city: City)

    @Query("DELETE FROM FavoritesCity WHERE name = :cityName")
    suspend fun deleteCity(cityName: String)

}