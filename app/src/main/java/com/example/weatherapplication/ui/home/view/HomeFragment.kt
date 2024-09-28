package com.example.weatherapplication.ui.home.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
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
import com.example.weatherapplication.utiltes.ForecastApiState
import com.example.weatherapplication.utiltes.InternetState
import com.example.weatherapplication.utiltes.WeatherApiState
import com.example.weatherapplication.utiltes.convertTemperature
import com.example.weatherapplication.utiltes.convertToLocalTime
import com.example.weatherapplication.utiltes.convertWindSpeed
import com.example.weatherapplication.utiltes.getWeatherIconResource
import com.example.weatherapplication.utiltes.parseIntegerIntoArabic
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import com.example.weatherapplication.data.pojo.Coord as CustomLocation

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var hourlyAdapter: HourlyAdapter
    private lateinit var dailyAdapter: DailyAdapter

    private lateinit var tempUnit: String
    private lateinit var windSpeed: String
    private var isNetwork = false

    private var _location = MutableStateFlow<CustomLocation>(CustomLocation(0.0, 0.0))

    private lateinit var fragmentHomeBinding: FragmentHomeBinding

    private val fusedLocationProviderClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireContext())
    }

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            getCurrentLocation()
        } else {
            Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false)

        val internetState = InternetState(requireActivity().application)

        val remoteDataSource = RemoteDataSource()
        val localDataSource = LocalDataSource(
            AppDatabase.getDatabase(requireContext()),
            SharedPreferences(requireContext())
        )

        homeViewModel = ViewModelProvider(
            this,
            HomeViewModelFactory(WeatherRepository(remoteDataSource, localDataSource),internetState)
        ).get(
            HomeViewModel::class.java
        )

        lifecycleScope.launch {
            homeViewModel.isInternetAvailable.collect { isAvailable ->
                if (isAvailable) {
                    isNetwork = true
                } else {
                    isNetwork = false
                }
            }
        }

        homeViewModel.observeNetwork()


        val latArg = arguments?.getFloat("lat")?.toDouble()
        val lonArg = arguments?.getFloat("lon")?.toDouble()


        if(latArg != null && latArg > 0 && lonArg != null && lonArg > 0)
        {
            _location.value.lat = latArg
            _location.value.lon = lonArg
        }
        else{
            checkLocationPermission()
        }




        lifecycleScope.launch {
            _location.collect { loc ->
                homeViewModel.apply {
                    fetchWeatherData(loc.lat, loc.lon,isNetwork)
                    fetchForecastData(loc.lat, loc.lon,isNetwork)
                }
            }
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

        lifecycleScope.launch {
            homeViewModel.weatherApiState.collect { state ->
                when (state) {
                    is WeatherApiState.Loading -> {
                        // Show loading UI
                        fragmentHomeBinding.weatherProgressBar.visibility = View.VISIBLE
                        fragmentHomeBinding.imgWeather.visibility = View.GONE
                    }
                    is WeatherApiState.Success -> {
                        // Update UI with weather data
                        val weatherResponse = state.weatherResponse

                        // Update your UI components here
                        fragmentHomeBinding.weatherProgressBar.visibility = View.GONE
                        fragmentHomeBinding.imgWeather.visibility = View.VISIBLE
                        updateUI(weatherResponse)
                    }
                    is WeatherApiState.Failure -> {
                        Toast.makeText(requireContext(), "Weather fetch error: ${state.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        lifecycleScope.launch {
            homeViewModel.forecastApiState.collect { state ->
                when (state) {
                    is ForecastApiState.Loading -> {
                        // Show loading UI for forecast
                        fragmentHomeBinding.daysProgressBar.visibility = View.VISIBLE
                        fragmentHomeBinding.recViewDays.visibility = View.GONE

                        fragmentHomeBinding.hoursProgressBar.visibility = View.VISIBLE
                        fragmentHomeBinding.recViewHourly.visibility = View.GONE

                    }
                    is ForecastApiState.Success -> {
                        val dailyForecasts = state.dailyForecasts
                        val hourlyForecasts = state.hourlyForecasts

                        // Update your UI components here
                        fragmentHomeBinding.daysProgressBar.visibility = View.GONE
                        fragmentHomeBinding.recViewDays.visibility = View.VISIBLE
                        updateDailyRecycler(dailyForecasts)

                        fragmentHomeBinding.hoursProgressBar.visibility = View.GONE
                        fragmentHomeBinding.recViewHourly.visibility = View.VISIBLE
                        updateHourlyRecycler(hourlyForecasts)
                    }
                    is ForecastApiState.Failure -> {
                        // Show error message for forecast
                        Toast.makeText(requireContext(), "Forecast fetch error: ${state.message}", Toast.LENGTH_SHORT).show()
                    }
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
            txtWeatherDeg.text = parseIntegerIntoArabic(
                convertTemperature(weatherResponse.main.temp, tempUnit),
                requireContext()
            )
            "H:${
                parseIntegerIntoArabic(
                    convertTemperature(weatherResponse.main.temp_max, tempUnit),
                    requireContext()
                )
            }  L:${
                parseIntegerIntoArabic(
                    convertTemperature(
                        weatherResponse.main.temp_min,
                        tempUnit
                    ), requireContext()
                )
            }".also { txtHAndLDeg.text = it }
            "${
                parseIntegerIntoArabic(
                    (weatherResponse.main.pressure).toString(),
                    requireContext()
                )
            } hPa".also { txtPressureDeg.text = it }
            "${
                parseIntegerIntoArabic(
                    (weatherResponse.main.humidity).toString(),
                    requireContext()
                )
            } %".also { txtHumidtyDeg.text = it }
            txtWindDeg.text = parseIntegerIntoArabic(
                convertWindSpeed(weatherResponse.wind.speed, windSpeed),
                requireContext()
            )
            "${
                parseIntegerIntoArabic(
                    (weatherResponse.clouds.all).toString(),
                    requireContext()
                )
            }%".also { txtCloudDeg.text = it }
            "${
                parseIntegerIntoArabic(
                    (weatherResponse.visibility).toString(),
                    requireContext()
                )
            } m".also { txtVisibiltyDeg.text = it }
            txtUVDeg.text = parseIntegerIntoArabic(
                weatherResponse.uV?.value?.toString() ?: "N/A",
                requireContext()
            )

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


    // Function to check permissions and request location
    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            getCurrentLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
            val androidLocation = task.result
            if (androidLocation != null) {
                val customLocation = CustomLocation(androidLocation.longitude, androidLocation.latitude)
                // Update the StateFlow or LiveData with the custom location
                _location.value = customLocation
            } else {
                val locationRequest = LocationRequest.Builder(
                    Priority.PRIORITY_HIGH_ACCURACY, 10000
                ).setMinUpdateIntervalMillis(5000)
                    .build()

                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult) {
                            val androidLastLocation = locationResult.lastLocation
                            androidLastLocation?.let {
                                val customLocation = CustomLocation(it.longitude, it.latitude)
                                _location.value = customLocation
                            }
                            fusedLocationProviderClient.removeLocationUpdates(this)
                        }
                    },
                    Looper.getMainLooper()
                )
            }
        }
    }


}