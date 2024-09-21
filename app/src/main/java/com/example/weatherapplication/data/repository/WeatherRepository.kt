package com.example.weatherapplication.data.repository


import com.example.weatherapplication.data.localdatasource.localdatsource.LocalDataSource
import com.example.weatherapplication.data.pojo.City
import com.example.weatherapplication.utiltes.formatDate
import com.example.weatherapplication.data.pojo.ForecastItem
import com.example.weatherapplication.data.pojo.WeatherResponse
import com.example.weatherapplication.data.remotedatasource.remotedatasource.RemoteDataSource

class WeatherRepository(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
    ) {

    suspend fun fetchWeather(lat: Double, lon: Double, apiKey: String): WeatherResponse? {
        val weatherResponse = remoteDataSource.getWeather(lat, lon, apiKey)
        val uvResponse = remoteDataSource.getUVIndex(lat, lon, apiKey)

        return if (weatherResponse.isSuccessful && uvResponse.isSuccessful) {
            weatherResponse.body()?.apply {
                uV = uvResponse.body()
            }
        } else {
            null // Handle errors or return a default object
        }
    }

    suspend fun fetchForecast(lat: Double, lon: Double, apiKey: String): Pair<List<ForecastItem>, List<ForecastItem>>? {
        val forecastResponse = remoteDataSource.getForecast(lat, lon, apiKey)

        return if (forecastResponse.isSuccessful) {
            forecastResponse.body()?.let { response ->

                // Skip the first element and then get distinct forecasts for days (by date)
                val dailyForecasts = response.list.drop(1).distinctBy { formatDate(it.dt, "EEE") }

                // Take the first 8 entries for hourly forecast
                val hourlyForecasts = response.list.take(8)

                Pair(dailyForecasts, hourlyForecasts)
            }
        } else {
            null // Handle errors
        }
    }

    suspend fun getAllCities(): List<City> {
        return localDataSource.getAllCities()
    }

    suspend fun insertCity(city: City) {
        localDataSource.insertCity(city)
    }

    suspend fun deleteCity(cityName: String) {
        localDataSource.deleteCity(cityName)
    }
}
