package com.example.weatherapplication.data.localdatasource.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapplication.data.pojo.AlarmData
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {
    @Query("SELECT * FROM alarm_table")
    fun getAllLocalAlarm(): Flow<List<AlarmData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarmData: AlarmData)

    @Delete
    suspend fun deleteAlarm(alarmData: AlarmData)

    @Query("DELETE FROM alarm_table WHERE time < :currentTimeMillis")
    suspend fun deleteOldAlarms(currentTimeMillis: Long)
}