package com.example.weatherapplication.data.localdatasource.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weatherapplication.data.pojo.AlarmData
import com.example.weatherapplication.data.pojo.City

@Database(entities = [City::class,AlarmData::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cityDao(): CityDao
    abstract fun alarmDao(): AlarmDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "weather_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}