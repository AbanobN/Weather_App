package com.example.weatherapplication.ui.home.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapplication.data.localdatasource.database.AppDatabase
import com.example.weatherapplication.data.localdatasource.localdatsource.LocalDataSource
import com.example.weatherapplication.data.localdatasource.sharedpreferences.SharedPreferences
import com.example.weatherapplication.data.pojo.ForecastItem
import com.example.weatherapplication.data.pojo.WeatherResponse
import com.example.weatherapplication.data.remotedatasource.remotedatasource.RemoteDataSource
import com.example.weatherapplication.data.repository.WeatherRepository
import com.example.weatherapplication.databinding.FragmentHomeBinding
import com.example.weatherapplication.ui.adapters.DailyAdapter
import com.example.weatherapplication.ui.adapters.HourlyAdapter
import com.example.weatherapplication.ui.home.viewmodel.HomeViewModel
import com.example.weatherapplication.ui.home.viewmodel.HomeViewModelFactory
import com.example.weatherapplication.utiltes.convertTemperature
import com.example.weatherapplication.utiltes.convertToLocalTime
import com.example.weatherapplication.utiltes.convertWindSpeed
import com.example.weatherapplication.utiltes.getWeatherIconResource
import com.example.weatherapplication.utiltes.parseIntegerIntoArabic
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var hourlyAdapter: HourlyAdapter
    private lateinit var dailyAdapter: DailyAdapter

    private lateinit var tempUnit: String
    private lateinit var windSpeed: String

    private var lat = 31.199004
    private var lon = 29.894378

    private lateinit var fragmentHomeBinding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false)

        val remoteDataSource = RemoteDataSource()
        val localDataSource = LocalDataSource(AppDatabase.getDatabase(requireContext()),
            SharedPreferences(requireContext())
        )

        homeViewModel = ViewModelProvider(this, HomeViewModelFactory(WeatherRepository(remoteDataSource,localDataSource))).get(
            HomeViewModel::class.java)
        Log.d("TAG", "onCreateView: ${arguments?.getFloat("lat")?.toDouble()} ")

        val latArg = arguments?.getFloat("lat")?.toDouble()
        val lonArg = arguments?.getFloat("lon")?.toDouble()

        lat = if (latArg != null && latArg > 0)  latArg else lat
        lon = if (lonArg != null && lonArg > 0)  lonArg else lon

        Log.d("TAG", "onCreateView: $lat , $lon")

        homeViewModel.apply {
            fetchWeatherData(lat, lon)
            fetchForecastData(lat, lon)
        }

        return fragmentHomeBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()

        homeViewModel.updateSettings()

        lifecycleScope.launch {
            homeViewModel.tempUnit.collect { unit ->
                tempUnit = unit
            }
        }

        lifecycleScope.launch {
            homeViewModel.windSpeed.collect { speed ->
                windSpeed = speed
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.weatherData.collect { weatherResponse ->
                    weatherResponse?.let { updateUI(it) }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED){
                homeViewModel.days.collect { dailyForecasts ->
                    updateDailyRecycler(dailyForecasts)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED){
                homeViewModel.hours.collect { hourlyForecasts ->
                    updateHourlyRecycler(hourlyForecasts)
                }
            }
        }
    }

    private fun setupRecyclerViews() {
        fragmentHomeBinding.recViewHourly.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        fragmentHomeBinding.recViewDays.layoutManager = LinearLayoutManager(context)
    }

    private fun updateUI(weatherResponse: WeatherResponse) {
        with(fragmentHomeBinding) {
            val localTime = convertToLocalTime(weatherResponse.dt, weatherResponse.timezone)

            txtCity.text = weatherResponse.name
            txtWeather.text = weatherResponse.weather[0].description
            "${localTime.first} | ${localTime.second}".also { txtDateAndTime.text = it }
            txtWeatherDeg.text = parseIntegerIntoArabic(convertTemperature(weatherResponse.main.temp, tempUnit),requireContext())
            "H:${parseIntegerIntoArabic(convertTemperature(weatherResponse.main.temp_max, tempUnit),requireContext())}  L:${parseIntegerIntoArabic( convertTemperature(weatherResponse.main.temp_min, tempUnit),requireContext())}".also { txtHAndLDeg.text = it }
            "${parseIntegerIntoArabic((weatherResponse.main.pressure).toString(),requireContext())} hPa".also { txtPressureDeg.text = it }
            "${parseIntegerIntoArabic((weatherResponse.main.humidity).toString(),requireContext())} %".also { txtHumidtyDeg.text = it }
            txtWindDeg.text = parseIntegerIntoArabic(convertWindSpeed(weatherResponse.wind.speed,windSpeed),requireContext())
            "${parseIntegerIntoArabic((weatherResponse.clouds.all).toString(),requireContext())}%".also { txtCloudDeg.text = it }
            "${parseIntegerIntoArabic((weatherResponse.visibility).toString(),requireContext())} m".also { txtVisibiltyDeg.text = it }
            txtUVDeg.text = parseIntegerIntoArabic(weatherResponse.uV?.value?.toString() ?: "N/A",requireContext())

            val weatherIconCode = weatherResponse.weather[0].icon
            val iconResource = getWeatherIconResource(weatherIconCode)
            imgWeather.setImageResource(iconResource)
        }
    }

    private fun updateHourlyRecycler(hourlyForecasts: List<ForecastItem>) {
        hourlyAdapter = HourlyAdapter(hourlyForecasts, tempUnit)
        fragmentHomeBinding.recViewHourly.adapter = hourlyAdapter
    }

    private fun updateDailyRecycler(dailyForecasts: List<ForecastItem>) {
        dailyAdapter = DailyAdapter(dailyForecasts, tempUnit)
        fragmentHomeBinding.recViewDays.adapter = dailyAdapter
    }
}