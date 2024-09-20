package com.example.weather_app_demo.data.pojo

data class WeatherResponse(
    val main: Main,
    val weather: List<Weather>,
    val visibility: Int,
    val wind: Wind,
    val clouds: Clouds,
    val dt: Long,
    val timezone: Int,
    val name: String,
    val coord: Coord,
    var uV : UVResponse? = null
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

data class Sys(
    val country: String
)


data class ForecastResponse(
    val list: List<ForecastItem>
)

data class City(
    val name: String,
    val coord: Coord
)


data class ForecastItem(
    val dt: Long,
    val main: Main,
    val weather: List<Weather>,
)

data class UVResponse(
    val value: Float
)




