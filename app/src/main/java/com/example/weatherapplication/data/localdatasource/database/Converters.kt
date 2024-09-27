package com.example.weatherapplication.data.localdatasource.database

import androidx.room.TypeConverter
import com.example.weatherapplication.data.pojo.Clouds
import com.example.weatherapplication.data.pojo.Coord
import com.example.weatherapplication.data.pojo.Main
import com.example.weatherapplication.data.pojo.UVResponse
import com.example.weatherapplication.data.pojo.Weather
import com.example.weatherapplication.data.pojo.Wind
import com.google.gson.Gson

class Converters {

    @TypeConverter
    fun fromMain(main: Main): String {
        return Gson().toJson(main)
    }

    @TypeConverter
    fun toMain(mainString: String): Main {
        return Gson().fromJson(mainString, Main::class.java)
    }

    @TypeConverter
    fun fromWeatherList(weatherList: List<Weather>): String {
        return Gson().toJson(weatherList)
    }

    @TypeConverter
    fun toWeatherList(weatherString: String): List<Weather> {
        val listType = object : com.google.gson.reflect.TypeToken<List<Weather>>() {}.type
        return Gson().fromJson(weatherString, listType)
    }

    @TypeConverter
    fun fromWind(wind: Wind): String {
        return Gson().toJson(wind)
    }

    @TypeConverter
    fun toWind(windString: String): Wind {
        return Gson().fromJson(windString, Wind::class.java)
    }

    @TypeConverter
    fun fromCoord(coord: Coord): String {
        return Gson().toJson(coord)
    }

    @TypeConverter
    fun toCoord(coordString: String): Coord {
        return Gson().fromJson(coordString, Coord::class.java)
    }

    @TypeConverter
    fun fromClouds(clouds: Clouds): String {
        return Gson().toJson(clouds)
    }

    @TypeConverter
    fun toClouds(cloudsString: String): Clouds {
        return Gson().fromJson(cloudsString, Clouds::class.java)
    }

    @TypeConverter
    fun fromUVResponse(uvResponse: UVResponse?): String {
        return Gson().toJson(uvResponse)
    }

    @TypeConverter
    fun toUVResponse(uvResponseString: String): UVResponse? {
        return Gson().fromJson(uvResponseString, UVResponse::class.java)
    }

}