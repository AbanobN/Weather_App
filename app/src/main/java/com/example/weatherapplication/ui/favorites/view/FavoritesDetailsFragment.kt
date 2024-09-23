package com.example.weatherapplication.ui.favorites.view

import android.os.Bundle
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
import com.example.weatherapplication.data.pojo.ForecastItem
import com.example.weatherapplication.data.pojo.WeatherResponse
import com.example.weatherapplication.data.remotedatasource.remotedatasource.RemoteDataSource
import com.example.weatherapplication.data.repository.WeatherRepository
import com.example.weatherapplication.databinding.FragmentFavoritesDetailsBinding
import com.example.weatherapplication.ui.adapters.DailyAdapter
import com.example.weatherapplication.ui.adapters.HourlyAdapter
import com.example.weatherapplication.ui.favorites.viewmodel.FavoritesDetailsViewModel
import com.example.weatherapplication.ui.favorites.viewmodel.FavoritesDetailsViewModelFactory
import com.example.weatherapplication.utiltes.convertTemperature
import com.example.weatherapplication.utiltes.convertWindSpeed
import com.example.weatherapplication.utiltes.getWeatherIconResource
import kotlinx.coroutines.launch


class FavoritesDetailsFragment : Fragment() {

    private lateinit var favoritesDetailsViewModel: FavoritesDetailsViewModel
    private lateinit var hourlyAdapter: HourlyAdapter
    private lateinit var dailyAdapter: DailyAdapter
    private val tempUnit = "C"

    private lateinit var favoritesDetailsBinding: FragmentFavoritesDetailsBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        favoritesDetailsBinding = FragmentFavoritesDetailsBinding.inflate(inflater, container, false)

        val remoteDataSource = RemoteDataSource()
        val localDataSource = LocalDataSource(AppDatabase.getDatabase(requireContext()))

        favoritesDetailsViewModel = ViewModelProvider(this, FavoritesDetailsViewModelFactory(
            WeatherRepository(remoteDataSource,localDataSource)
        )).get(
        FavoritesDetailsViewModel::class.java)

        val lon = arguments?.getDouble("lon") ?: 0.0
        val lat = arguments?.getDouble("lat") ?: 0.0
        val apiKey = "88be804d07441dfca3b574fec6dda8e7"

        favoritesDetailsViewModel.apply {
            fetchWeatherData(lat, lon, apiKey)
            fetchForecastData(lat, lon, apiKey)
        }

        return favoritesDetailsBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViews()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED){
                favoritesDetailsViewModel.weatherData.collect { weatherResponse ->
                    weatherResponse?.let { updateUI(it) }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED){
                favoritesDetailsViewModel.days.collect { dailyForecasts ->
                    updateDailyRecycler(dailyForecasts)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED){
                favoritesDetailsViewModel.hours.collect { hourlyForecasts ->
                    updateHourlyRecycler(hourlyForecasts)
                }
            }
        }
    }

    private fun setupRecyclerViews() {
        favoritesDetailsBinding.recViewHourly.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        favoritesDetailsBinding.recViewDays.layoutManager = LinearLayoutManager(context)
    }

    private fun updateUI(weatherResponse: WeatherResponse) {
        with(favoritesDetailsBinding) {
            txtCity.text = weatherResponse.name
            txtWeather.text = weatherResponse.weather[0].description
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
        favoritesDetailsBinding.recViewHourly.adapter = hourlyAdapter
    }

    private fun updateDailyRecycler(dailyForecasts: List<ForecastItem>) {
        dailyAdapter = DailyAdapter(dailyForecasts, tempUnit)
        favoritesDetailsBinding.recViewDays.adapter = dailyAdapter
    }
}