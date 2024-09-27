package com.example.weatherapplication.data.remotedatasource.fakeremotedatasource

import com.example.weatherapplication.data.pojo.Clouds
import com.example.weatherapplication.data.pojo.Coord
import com.example.weatherapplication.data.pojo.ForecastItem
import com.example.weatherapplication.data.pojo.ForecastResponse
import com.example.weatherapplication.data.pojo.LocationResponse
import com.example.weatherapplication.data.pojo.Main
import com.example.weatherapplication.data.pojo.UVResponse
import com.example.weatherapplication.data.pojo.Weather
import com.example.weatherapplication.data.pojo.WeatherResponse
import com.example.weatherapplication.data.pojo.Wind
import com.example.weatherapplication.data.remotedatasource.remotedatasource.IRemoteDataSource


class FakeRemoteDataSource : IRemoteDataSource {

    // Mock responses for testing

    var shouldThrowError = false

    var mockWeatherResponse = WeatherResponse(
        main = Main(temp = 25.0, temp_min = 20.0, temp_max = 30.0, pressure = 1013, humidity = 50),
        weather = listOf(Weather(description = "Clear sky", icon = "01d")),
        visibility = 10000,
        wind = Wind(speed = 5.0),
        clouds = Clouds(all = 0),
        dt = System.currentTimeMillis(),
        timezone = 3600,
        coord = Coord(lon = 31.2357, lat = 30.0444),
        name = "Cairo"
    )

    private var mockLocationResponse = listOf(
        LocationResponse(name = "Cairo", lon = 31.2357, lat = 30.0444),
        LocationResponse(name = "Alexandria", lon = 29.9187, lat = 31.2001)
    )

    private var mockUVResponse = UVResponse(
        value = 5.0f
    )

    var mockForecastResponse = ForecastResponse(
        list = listOf(
            ForecastItem(
                id = 1,
                dt = System.currentTimeMillis() + 86400 * 1000, // 1 day later
                main = Main(temp = 26.0, temp_min = 22.0, temp_max = 28.0, pressure = 1015, humidity = 55),
                weather = listOf(Weather(description = "Partly cloudy", icon = "02d")),
                type = "days"
            ),
            ForecastItem(
                id = 2,
                dt = System.currentTimeMillis() + 172800 * 1000, // 2 days later
                main = Main(temp = 24.0, temp_min = 21.0, temp_max = 26.0, pressure = 1010, humidity = 60),
                weather = listOf(Weather(description = "Rain", icon = "10d")),
                type = "hours"
            )
        )
    )

    override suspend fun getWeather(lat: Double, lon: Double, lan: String): WeatherResponse {
        if (shouldThrowError) {
            throw Exception("Mock error: Unable to fetch weather data")
        }
        return mockWeatherResponse
    }

    override suspend fun getLocationByCoordinates(lat: Double, lon: Double): List<LocationResponse> {
        return mockLocationResponse
    }

    override suspend fun getLocationByCityName(cityName: String): List<LocationResponse> {
        return mockLocationResponse.filter { it.name.equals(cityName, ignoreCase = true) }
    }

    override suspend fun getUVIndex(lat: Double, lon: Double): UVResponse {
        return mockUVResponse
    }

    override suspend fun getForecast(lat: Double, lon: Double, lan: String): ForecastResponse {
        if (shouldThrowError) {
            throw Exception("Mock error: Unable to fetch Forecast data")
        }
        return mockForecastResponse
    }
}