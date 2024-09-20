package com.example.weatherapplication.ui.home.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapplication.data.pojo.ForecastItem
import com.example.weatherapplication.data.pojo.WeatherResponse
import com.example.weatherapplication.databinding.FragmentHomeBinding
import com.example.weatherapplication.ui.home.viewmodel.HomeViewModel
import com.example.weatherapplication.utiltes.convertTemperature
import com.example.weatherapplication.utiltes.convertToLocalTime
import com.example.weatherapplication.utiltes.convertWindSpeed
import com.example.weatherapplication.utiltes.getWeatherIconResource

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var hourlyAdapter: HourlyAdapter
    private lateinit var dailyAdapter: DailyAdapter
    private val tempUnit = "C"

    private lateinit var fragmentHomeBinding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false)
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        val lat = 31.205753
        val lon = 29.924526
        val apiKey = "88be804d07441dfca3b574fec6dda8e7"

        homeViewModel.apply {
            fetchWeather(lat, lon, apiKey)
            fetchForecast(lat, lon, apiKey)
        }

        return fragmentHomeBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()

        homeViewModel.weatherData.observe(viewLifecycleOwner) { weatherResponse ->
            weatherResponse?.let { updateUI(it) }
        }

        homeViewModel.days.observe(viewLifecycleOwner) { dailyForecasts ->
            updateDailyRecycler(dailyForecasts)
        }

        homeViewModel.hours.observe(viewLifecycleOwner) { hourlyForecasts ->
            updateHourlyRecycler(hourlyForecasts)
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
            txtWeatherDeg.text = convertTemperature(weatherResponse.main.temp, tempUnit)
            "H:${convertTemperature(weatherResponse.main.temp_max, tempUnit)}  L:${convertTemperature(weatherResponse.main.temp_min, tempUnit)}".also { txtHAndLDeg.text = it }
            "${weatherResponse.main.pressure} hPa".also { txtPressureDeg.text = it }
            "${weatherResponse.main.humidity} %".also { txtHumidtyDeg.text = it }
            txtWindDeg.text = convertWindSpeed(weatherResponse.wind.speed)
            "${weatherResponse.clouds.all}%".also { txtCloudDeg.text = it }
            "${weatherResponse.visibility} m".also { txtVisibiltyDeg.text = it }
            txtUVDeg.text = weatherResponse.uV?.value?.toString() ?: "N/A"

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