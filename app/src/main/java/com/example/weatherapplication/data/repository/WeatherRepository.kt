package com.example.weatherapplication.data.repository

import com.example.weatherapplication.data.localdatasource.localdatsource.LocalDataSource
import com.example.weatherapplication.data.localdatasource.sharedpreferences.SharedPreferences
import com.example.weatherapplication.data.pojo.AlarmData
import com.example.weatherapplication.data.pojo.City
import com.example.weatherapplication.data.pojo.ForecastItem
import com.example.weatherapplication.data.pojo.LocationResponse
import com.example.weatherapplication.data.pojo.WeatherResponse
import com.example.weatherapplication.data.remotedatasource.remotedatasource.RemoteDataSource
import com.example.weatherapplication.utiltes.formatDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList

class WeatherRepository(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
    ) {


    fun fetchWeather(lat: Double, lon: Double, lan:String=localDataSource.getLan()): Flow<WeatherResponse> = flow {
        try {
            val weatherResponse = remoteDataSource.getWeather(lat, lon,lan)
            val uvResponse = remoteDataSource.getUVIndex(lat, lon)
            val locationResponse = remoteDataSource.getLocationByCoordinates(lat, lon).first()

            weatherResponse.apply {
                uV = uvResponse
                name = locationResponse.name
            }

            emit(weatherResponse)
        }catch(e: Exception) {
            throw Exception("Error fetching location: ${e.message}")
        }
    }

    fun fetchWeatherAndUpdate(lat: Double, lon: Double, lan:String=localDataSource.getLan() , isNetwork:Boolean): Flow<WeatherResponse> = flow {
        if(isNetwork)
        {
            try {
                val weatherResponse = remoteDataSource.getWeather(lat, lon,lan)
                val uvResponse = remoteDataSource.getUVIndex(lat, lon)
                val locationResponse = remoteDataSource.getLocationByCoordinates(lat, lon).first()

                weatherResponse.apply {
                    uV = uvResponse
                    name = locationResponse.name
                }

                localDataSource.deleteAllWeather()
                localDataSource.insertWeather(weatherResponse)

                emit(weatherResponse)
            }catch(e: Exception) {
                throw Exception("Error fetching location: ${e.message}")
            }
        }
        else{
            localDataSource.getLastWeather().collect { weatherResponse ->
                weatherResponse?.let { emit(it) }
            }
        }
    }


    fun fetchForecast(lat: Double, lon: Double, lan:String=localDataSource.getLan()): Flow<Pair<List<ForecastItem>, List<ForecastItem>>> = flow {
        try {
            val forecastResponse = remoteDataSource.getForecast(lat, lon,lan)
            forecastResponse.let { response ->
                val dailyForecasts = response.list.drop(1).distinctBy { formatDate(it.dt, "EEE") }
                val hourlyForecasts = response.list.take(8)
                emit(Pair(dailyForecasts, hourlyForecasts))
            }
        } catch (e: Exception) {
            throw Exception("Error fetching forecast: ${e.message}")
        }
    }

    fun fetchForecastAndUpdate(lat: Double, lon: Double, lan:String=localDataSource.getLan() , isNetwork: Boolean): Flow<Pair<List<ForecastItem>, List<ForecastItem>>> = flow {
        if(isNetwork)
        {
            try {
                val forecastResponse = remoteDataSource.getForecast(lat, lon,lan)
                forecastResponse.let { response ->
                    val dailyForecasts = response.list.drop(1).distinctBy { formatDate(it.dt, "EEE") }
                    val hourlyForecasts = response.list.take(8)

                    val forecastItems = dailyForecasts.map { item ->
                        ForecastItem(
                            dt = item.dt,
                            main = item.main,
                            weather = item.weather,
                            type = "days" // Set the type as "days"
                        )
                    } + hourlyForecasts.map { item ->
                        ForecastItem(
                            dt = item.dt,
                            main = item.main,
                            weather = item.weather,
                            type = "hours" // Set the type as "hours"
                        )
                    }

                    localDataSource.deleteAllForecastItems()

                    localDataSource.insertForecastItems(forecastItems)

                    emit(Pair(dailyForecasts, hourlyForecasts))
                }
            } catch (e: Exception) {
                throw Exception("Error fetching forecast: ${e.message}")
            }
        }else{

            val lastDailyForecasts = mutableListOf<ForecastItem>()
            val lastHourlyForecasts = mutableListOf<ForecastItem>()

            // Collect daily forecasts
            localDataSource.getForecastItemsByType("days").collect { items ->
                lastDailyForecasts.addAll(items)
            }

            // Collect hourly forecasts
            localDataSource.getForecastItemsByType("hours").collect { items ->
                lastHourlyForecasts.addAll(items)
            }


            emit(Pair(lastDailyForecasts, lastHourlyForecasts))
        }
    }

    fun getLocationByCoordinates(lat: Double, lon: Double): Flow<LocationResponse> = flow {
        try {
            val locationResponse = remoteDataSource.getLocationByCoordinates(lat, lon).first()
            emit(locationResponse)
        } catch (e: Exception) {
            throw Exception("Error fetching location by coordinates: ${e.message}")
        }
    }

    fun getLocationByCityName(cityName: String): Flow<LocationResponse> = flow {
        try {
            val locationResponse = remoteDataSource.getLocationByCityName(cityName).first()
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

    fun getAllLocalAlarm(): Flow<List<AlarmData>> {
        return localDataSource.getAllLocalAlarm()
    }

    suspend fun insertAlarmData(alarmData: AlarmData) {
        localDataSource.insertAlarmData(alarmData)
    }

    suspend fun deletAlarm(alarmData: AlarmData) {
        localDataSource.deletAlarm(alarmData)
    }

    suspend fun deleteOldAlarms(currentTimeMillis: Long) {
        localDataSource.deleteOldAlarms(currentTimeMillis)
    }

}
