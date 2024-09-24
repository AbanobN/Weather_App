package com.example.weatherapplication.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.weatherapplication.R
import com.example.weatherapplication.data.localdatasource.database.AppDatabase
import com.example.weatherapplication.data.localdatasource.localdatsource.LocalDataSource
import com.example.weatherapplication.data.localdatasource.sharedpreferences.SharedPreferences
import com.example.weatherapplication.data.remotedatasource.remotedatasource.RemoteDataSource
import com.example.weatherapplication.data.repository.WeatherRepository
import com.example.weatherapplication.databinding.FragmentSettingsBinding
import com.example.weatherapplication.ui.home.viewmodel.HomeViewModelFactory
import com.example.weatherapplication.ui.map.viewmodel.MapViewModel
import com.example.weatherapplication.ui.map.viewmodel.MapViewModelFactory
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private lateinit var _binding: FragmentSettingsBinding
    private lateinit var  settingsViewModel:SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val remoteDataSource = RemoteDataSource()
        val localDataSource = LocalDataSource(
            AppDatabase.getDatabase(requireContext()),
            SharedPreferences(requireContext())
        )
        settingsViewModel = ViewModelProvider(this, SettingViewModelFactory(WeatherRepository(remoteDataSource,localDataSource))).get(
            SettingsViewModel::class.java)

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = _binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settingsViewModel.getSettings()

        lifecycleScope.launch{
            settingsViewModel.settings.collect { settings ->
                updateRadioGroups(settings)
            }
        }

        _binding.groupLocation.setOnCheckedChangeListener{ group , checkedId ->
            when(checkedId){
                R.id.map -> settingsViewModel.saveLocation("map")
                else -> settingsViewModel.saveLocation("gps")
            }

        }

        _binding.groupLanguage.setOnCheckedChangeListener{ _ , checkedId ->
            when(checkedId){
                R.id.arabic -> settingsViewModel.saveLan("ar")
                else -> settingsViewModel.saveLan("en")
            }
        }

        _binding.groupTemperature.setOnCheckedChangeListener{ _ , checkedId ->
            when(checkedId){
                R.id.kelvin -> settingsViewModel.saveUnit("K")
                R.id.fahrenheit -> settingsViewModel.saveUnit("F")
                else -> settingsViewModel.saveUnit("C")
            }
        }

        _binding.groupWindSpeed.setOnCheckedChangeListener{ _ , checkedId ->
            when(checkedId){
                R.id.mile_hour -> settingsViewModel.saveSpeed("mph")
                else -> settingsViewModel.saveSpeed("mps")
            }
        }

        _binding.groupNotifications.setOnCheckedChangeListener{ _ , checkedId ->
            when(checkedId){
                R.id.disable -> settingsViewModel.saveNotification("disable")
                else -> settingsViewModel.saveNotification("enable")
            }
        }
    }

    private fun updateRadioGroups(settings: List<String>) {
        when (settings[0]) {
            "gps" -> _binding.groupLocation.check(R.id.gps)
            "map" -> _binding.groupLocation.check(R.id.map)
        }
        when (settings[1]) {
            "en" -> _binding.groupLanguage.check(R.id.english)
            "ar" -> _binding.groupLanguage.check(R.id.arabic)
        }
        when (settings[2]) {
            "C" -> _binding.groupTemperature.check(R.id.celsius)
            "K" -> _binding.groupTemperature.check(R.id.kelvin)
            "F" -> _binding.groupTemperature.check(R.id.fahrenheit)
        }
        when (settings[3]) {
            "mps" -> _binding.groupWindSpeed.check(R.id.meter_sec)
            "mph" -> _binding.groupWindSpeed.check(R.id.mile_hour)
        }
        when (settings[4]) {
            "enable" -> _binding.groupNotifications.check(R.id.enable)
            "disable" -> _binding.groupNotifications.check(R.id.disable)
        }
    }
}