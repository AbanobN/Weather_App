package com.example.weatherapplication.ui.alerts.viewmodel

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapplication.data.pojo.AlarmData
import com.example.weatherapplication.data.repository.IWeatherRepository
import com.example.weatherapplication.ui.alerts.view.AlarmReceiver
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

    fun deleteAlarm(context: Context,alarmData: AlarmData) {

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmRequestCode = alarmData.requestCode

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = "ALARM"
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmRequestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)

        pendingIntent.cancel()

        viewModelScope.launch {
            weatherRepository.deletAlarm(alarmData)
        }

    }



}