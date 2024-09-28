package com.example.weatherapplication.ui.alerts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapplication.data.pojo.AlarmData
import com.example.weatherapplication.data.repository.IWeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AlertsViewModel(private val weatherRepository: IWeatherRepository): ViewModel() {

    private val _alarms= MutableStateFlow<List<AlarmData>>(emptyList())
    val alarms : StateFlow<List<AlarmData>> get() = _alarms


    init {
        val currentTimeInMillis = System.currentTimeMillis()
        deleteOldAlarms(currentTimeInMillis)
        getAlarms()
    }


    fun getAlarms()
    {
        viewModelScope.launch {
            weatherRepository.getAllLocalAlarm().collect{
                _alarms.value=it
            }
        }
    }

    fun insertAlarm(alarmData: AlarmData)
    {
        viewModelScope.launch {
            weatherRepository.insertAlarmData(alarmData)
        }
    }

    fun deleteOldAlarms(currentTimeMillis: Long)
    {
        viewModelScope.launch {
            weatherRepository.deleteOldAlarms(currentTimeMillis)
        }
    }


}