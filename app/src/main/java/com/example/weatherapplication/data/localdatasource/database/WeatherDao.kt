package com.example.weatherapplication.data.localdatasource.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapplication.data.pojo.WeatherResponse

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weatherResponse: WeatherResponse)

    @Query("SELECT * FROM last_weather ORDER BY dt DESC LIMIT 1")
    suspend fun getLastWeather(): WeatherResponse?

    @Query("DELETE FROM last_weather")
    suspend fun deleteAllWeather()
}