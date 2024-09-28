package com.example.weatherapplication.data.repository

import com.example.weatherapplication.data.localdatasource.localdatsource.ILocalDataSource
import com.example.weatherapplication.data.pojo.AlarmData
import com.example.weatherapplication.data.pojo.City
import com.example.weatherapplication.data.pojo.ForecastItem
import com.example.weatherapplication.data.pojo.LocationResponse
import com.example.weatherapplication.data.pojo.WeatherResponse
import com.example.weatherapplication.data.remotedatasource.remotedatasource.IRemoteDataSource
import com.example.weatherapplication.utiltes.formatDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class WeatherRepository(
    private val remoteDataSource: IRemoteDataSource,
    private val localDataSource: ILocalDataSource
    ) : IWeatherRepository {


    override fun fetchWeather(lat: Double, lon: Double): Flow<WeatherResponse> = flow {
        try {
            val weatherResponse = remoteDataSource.getWeather(lat, lon,lan = localDataSource.getLan())
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

    override fun fetchWeatherAndUpdate(lat: Double, lon: Double, isNetwork:Boolean): Flow<WeatherResponse> = flow {
        if(isNetwork)
        {
            try {
                val weatherResponse = remoteDataSource.getWeather(lat, lon,lan = localDataSource.getLan())
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


    override fun fetchForecast(lat: Double, lon: Double): Flow<Pair<List<ForecastItem>, List<ForecastItem>>> = flow {
        try {
            val forecastResponse = remoteDataSource.getForecast(lat, lon,lan = localDataSource.getLan())
            forecastResponse.let { response ->
                val dailyForecasts = response.list.drop(1).distinctBy { formatDate(it.dt, "EEE") }
                val hourlyForecasts = response.list.take(8)
                emit(Pair(dailyForecasts, hourlyForecasts))
            }
        } catch (e: Exception) {
            throw Exception("Error fetching forecast: ${e.message}")
        }
    }

    override fun fetchForecastAndUpdate(lat: Double, lon: Double, isNetwork: Boolean): Flow<Pair<List<ForecastItem>, List<ForecastItem>>> = flow {
        if(isNetwork)
        {
            try {
                val forecastResponse = remoteDataSource.getForecast(lat, lon,lan=localDataSource.getLan())
                forecastResponse.let { response ->
                    val dailyForecasts = response.list.distinctBy { formatDate(it.dt, "EEE") }.drop(1)
                    val hourlyForecasts = response.list.take(8)

                    val forecastItems = dailyForecasts.map { item ->
                        ForecastItem(
                            dt = item.dt,
                            main = item.main,
                            weather = item.weather,
                            type = "days"
                        )
                    } + hourlyForecasts.map { item ->
                        ForecastItem(
                            dt = item.dt,
                            main = item.main,
                            weather = item.weather,
                            type = "hours"
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

            val lastDailyForecasts = localDataSource.getForecastItemsByType("days").first()
            val lastHourlyForecasts = localDataSource.getForecastItemsByType("hours").first()

            emit(Pair(lastDailyForecasts, lastHourlyForecasts))
        }
    }

    override fun getLocationByCoordinates(lat: Double, lon: Double): Flow<LocationResponse> = flow {
        try {
            val locationResponse = remoteDataSource.getLocationByCoordinates(lat, lon).first()
            emit(locationResponse)
        } catch (e: Exception) {
            throw Exception("Error fetching location by coordinates: ${e.message}")
        }
    }

    override fun getLocationByCityName(cityName: String): Flow<LocationResponse> = flow {
        try {
            val locationResponse = remoteDataSource.getLocationByCityName(cityName).first()
            emit(locationResponse)
        } catch (e: Exception) {
            throw Exception("Error fetching location by coordinates: ${e.message}")
        }
    }


    override fun getAllCities(): Flow<List<City>> {
        return localDataSource.getAllCities()
    }

    override suspend fun insertCity(city: City) {
        localDataSource.insertCity(city)
    }

    override suspend fun deleteCity(cityName: String) {
        localDataSource.deleteCity(cityName)
    }

    override fun setLan(lan : String){
        localDataSource.setLan(lan)
    }
    override fun getLan() : String{
        return localDataSource.getLan()
    }

    override fun setSpeed(speed : String){
        localDataSource.setSpeed(speed)
    }
    override fun getSpeed() : String{
        return localDataSource.getSpeed()
    }

    override fun setUnit(unit : String){
        localDataSource.setUnit(unit)
    }
    override fun getUnit() : String{
        return localDataSource.getUnit()
    }
    override fun setLocation(location: String){
        localDataSource.setLocation(location)
    }
    override fun getLocation(): String{
        return localDataSource.getLocation()
    }
    override fun setNotification(notification: String){
        localDataSource.setNotification(notification)
    }
    override fun getNotification(): String{
        return localDataSource.getNotification()
    }

    override fun getAllLocalAlarm(): Flow<List<AlarmData>> {
        return localDataSource.getAllLocalAlarm()
    }

    override suspend fun insertAlarmData(alarmData: AlarmData) {
        localDataSource.insertAlarmData(alarmData)
    }

    override suspend fun deletAlarm(alarmData: AlarmData) {
        localDataSource.deletAlarm(alarmData)
    }

    override suspend fun deleteOldAlarms(currentTimeMillis: Long) {
        localDataSource.deleteOldAlarms(currentTimeMillis)
    }

}
