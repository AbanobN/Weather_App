package com.example.weatherapplication.ui.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapplication.data.repository.IWeatherRepository
import com.example.weatherapplication.utiltes.InternetState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(private val weatherRepository: IWeatherRepository,
                        private val internetState: InternetState)  : ViewModel() {


    private val _settings = MutableStateFlow<List<String>>(emptyList())
    val settings: StateFlow<List<String>> get() = _settings

    private val _isInternetAvailable = MutableStateFlow(false)
    val isInternetAvailable: StateFlow<Boolean> = _isInternetAvailable


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

    fun observeNetwork() {
        viewModelScope.launch {
            val isAvailable = internetState.isInternetAvailable()
            _isInternetAvailable.value = isAvailable
        }
    }


}