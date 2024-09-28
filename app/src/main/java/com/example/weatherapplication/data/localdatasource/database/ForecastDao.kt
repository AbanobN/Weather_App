package com.example.weatherapplication.data.localdatasource.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapplication.data.pojo.ForecastItem
import kotlinx.coroutines.flow.Flow


@Dao
interface ForecastDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForecastItems(forecastItems: List<ForecastItem>)

    @Query("SELECT * FROM forecast_items WHERE type = :type")
    fun getForecastItemsByType(type: String): Flow<List<ForecastItem>>

    @Query("DELETE FROM forecast_items")
    suspend fun deleteAllForecastItems()
}