package com.example.weatherapplication.data.pojo

import androidx.annotation.Nullable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "last_weather")
data class WeatherResponse(
    val main: Main,
    val weather: List<Weather>,
    val visibility: Int,
    val wind: Wind,
    val clouds: Clouds,
    val dt: Long,
    val timezone: Int,
    val coord: Coord,
    @PrimaryKey var name: String,
    var uV : UVResponse? = null,
)


data class Coord(val lon: Double, val lat: Double)

data class Weather(
    val description: String,
    val icon: String
)

data class Main(
    val temp: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure: Int,
    val humidity: Int
)


data class Wind(val speed: Double)


data class Clouds(val all: Int)

data class LocationResponse(
    val name: String,
    val lon: Double,
    val lat: Double
)


data class ForecastResponse(
    val list: List<ForecastItem>
)

@Entity(tableName = "FavoritesCity")
data class City(
    @PrimaryKey val name: String,
    @Embedded val coord: Coord
)

@Entity(tableName = "forecast_items")
data class ForecastItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dt: Long,
    val main: Main,
    val weather: List<Weather>,
    var type: String?
)

data class UVResponse(
    val value: Float
)

@Entity(tableName = "alarm_table")
data class AlarmData(
    @PrimaryKey val requestCode :Int,
    val time:Long
)


// not needed
data class Location(
    var latitude : Double,
    var longitude : Double
)




