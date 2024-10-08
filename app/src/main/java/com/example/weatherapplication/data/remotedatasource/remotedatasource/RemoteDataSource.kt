package com.example.weatherapplication.data.remotedatasource.remotedatasource


import com.example.weatherapplication.data.pojo.ForecastResponse
import com.example.weatherapplication.data.pojo.LocationResponse
import com.example.weatherapplication.data.pojo.UVResponse
import com.example.weatherapplication.data.pojo.WeatherResponse
import com.example.weatherapplication.data.remotedatasource.retrofit.RetrofitInstance

class RemoteDataSource : IRemoteDataSource {

    private val weatherService = RetrofitInstance.weatherService

    override suspend fun getWeather(lat: Double, lon: Double, lan:String): WeatherResponse {
        return weatherService.getWeather(lat,lon,lan)
    }

    override suspend fun getLocationByCoordinates(lat: Double, lon: Double): List<LocationResponse> {
        return weatherService.getLocationByCoordinates(lat,lon)
    }
    override suspend fun getLocationByCityName(cityName: String): List<LocationResponse>{
        return  weatherService.getLocationByName(cityName)
    }

    override suspend fun getUVIndex(lat: Double, lon: Double): UVResponse {
        return weatherService.getUVIndex(lat, lon)
    }

    override suspend fun getForecast(lat: Double, lon: Double, lan:String): ForecastResponse {
        return weatherService.getForecast(lat, lon, lan)
    }
}
