package com.example.weatherapplication.data.repository

import com.example.weatherapplication.data.localdatasource.localdatsource.LocalDataSource
import com.example.weatherapplication.data.localdatasource.sharedpreferences.SharedPreferences
import com.example.weatherapplication.data.pojo.City
import com.example.weatherapplication.data.pojo.ForecastItem
import com.example.weatherapplication.data.pojo.LocationResponse
import com.example.weatherapplication.data.pojo.WeatherResponse
import com.example.weatherapplication.data.remotedatasource.remotedatasource.RemoteDataSource
import com.example.weatherapplication.utiltes.formatDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WeatherRepository(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
    ) {

    fun fetchWeather(lat: Double, lon: Double, lan:String=localDataSource.getLan(), apiKey: String): Flow<WeatherResponse> = flow {
        try {
            val weatherResponse = remoteDataSource.getWeather(lat, lon,lan ,apiKey)
            val uvResponse = remoteDataSource.getUVIndex(lat, lon, apiKey)
            val locationResponse = remoteDataSource.getLocationByCoordinates(lat, lon, apiKey).first()

            weatherResponse.apply {
                uV = uvResponse
                name = locationResponse.name
            }
            emit(weatherResponse)
        }catch(e: Exception) {
            throw Exception("Error fetching location: ${e.message}")
        }
    }


    fun fetchForecast(lat: Double, lon: Double, lan:String=localDataSource.getLan(), apiKey: String): Flow<Pair<List<ForecastItem>, List<ForecastItem>>> = flow {
        try {
            val forecastResponse = remoteDataSource.getForecast(lat, lon,lan , apiKey)
            forecastResponse.let { response ->
                val dailyForecasts = response.list.drop(1).distinctBy { formatDate(it.dt, "EEE") }
                val hourlyForecasts = response.list.take(8)
                emit(Pair(dailyForecasts, hourlyForecasts))
            }
        } catch (e: Exception) {
            throw Exception("Error fetching forecast: ${e.message}")
        }
    }

    fun getLocationByCoordinates(lat: Double, lon: Double, apiKey: String): Flow<LocationResponse> = flow {
        try {
            val locationResponse = remoteDataSource.getLocationByCoordinates(lat, lon, apiKey).first()
            emit(locationResponse)
        } catch (e: Exception) {
            throw Exception("Error fetching location by coordinates: ${e.message}")
        }
    }

    fun getLocationByCityName(cityName: String, apiKey: String): Flow<LocationResponse> = flow {
        try {
            val locationResponse = remoteDataSource.getLocationByCityName(cityName, apiKey).first()
            emit(locationResponse)
        } catch (e: Exception) {
            throw Exception("Error fetching location by coordinates: ${e.message}")
        }
    }


    fun getAllCities(): Flow<List<City>> {
        return localDataSource.getAllCities()
    }

    suspend fun insertCity(city: City) {
        localDataSource.insertCity(city)
    }

    suspend fun deleteCity(cityName: String) {
        localDataSource.deleteCity(cityName)
    }

    fun setLan(lan : String){
        localDataSource.setLan(lan)
    }
    fun getLan() : String{
        return localDataSource.getLan()
    }

    fun setSpeed(speed : String){
        localDataSource.setSpeed(speed)
    }
    fun getSpeed() : String{
        return localDataSource.getSpeed()
    }

    fun setUnit(unit : String){
        localDataSource.setUnit(unit)
    }
    fun getUnit() : String{
        return localDataSource.getUnit()
    }
    fun setLocation(location: String){
        localDataSource.setLocation(location)
    }
    fun getLocation(): String{
        return localDataSource.getLocation()
    }
    fun setNotification(notification: String){
        localDataSource.setNotification(notification)
    }
    fun getNotification(): String{
        return localDataSource.getNotification()
    }

}
