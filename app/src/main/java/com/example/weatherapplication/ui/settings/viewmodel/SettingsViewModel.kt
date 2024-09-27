package com.example.weatherapplication.ui.settings.viewmodel

import androidx.lifecycle.ViewModel
import com.example.weatherapplication.data.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel(private val weatherRepository: WeatherRepository)  : ViewModel() {


    private val _settings = MutableStateFlow<List<String>>(emptyList())
    val settings: StateFlow<List<String>> get() = _settings

    fun saveLocation(location: String){
        weatherRepository.setLocation(location)
    }
    fun saveLan(lan: String){
        weatherRepository.setLan(lan)
    }
    fun saveUnit(unit: String){
        weatherRepository.setUnit(unit)
    }
    fun saveSpeed(speed: String){
        weatherRepository.setSpeed(speed)
    }
    fun saveNotification(notification:String){
        weatherRepository.setNotification(notification)
    }

    fun getSettings(){
        _settings.value = listOf(
            weatherRepository.getLocation(),
            weatherRepository.getLan(),
            weatherRepository.getUnit(),
            weatherRepository.getSpeed(),
            weatherRepository.getNotification()
        )
    }


}