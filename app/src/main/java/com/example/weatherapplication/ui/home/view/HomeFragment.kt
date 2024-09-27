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
            // Permission granted, call getCurrentLocation
            getCurrentLocation()
        } else {
            // Permission denied, handle the case
            Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false)


        val remoteDataSource = RemoteDataSource()
        val localDataSource = LocalDataSource(
            AppDatabase.getDatabase(requireContext()),
            SharedPreferences(requireContext())
        )

        homeViewModel = ViewModelProvider(
            this,
            HomeViewModelFactory(WeatherRepository(remoteDataSource, localDataSource))
        ).get(
            HomeViewModel::class.java
        )

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
                    fetchWeatherData(loc.lat, loc.lon)
                    fetchForecastData(loc.lat, loc.lon)
                    Log.d("TAG", "onCreateView: ${loc.lat} , ${loc.lon}")
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

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.weatherData.collect { weatherResponse ->
                    weatherResponse?.let { updateUI(it) }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                homeViewModel.days.collect { dailyForecasts ->
                    updateDailyRecycler(dailyForecasts)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
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
            // Request location permissions
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            // Permission already granted, get location
            getCurrentLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
            val androidLocation = task.result
            if (androidLocation != null) {
                // Convert Android Location to Custom Location
                val customLocation = CustomLocation(androidLocation.longitude, androidLocation.latitude)
                // Update the StateFlow or LiveData with the custom location
                _location.value = customLocation
            } else {
                // Create a proper LocationRequest
                val locationRequest = LocationRequest.Builder(
                    Priority.PRIORITY_HIGH_ACCURACY, 10000 // Every 10 seconds
                ).setMinUpdateIntervalMillis(5000) // Minimum interval of 5 seconds
                    .build()

                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult) {
                            val androidLastLocation = locationResult.lastLocation
                            androidLastLocation?.let {
                                // Convert Android Location to Custom Location
                                val customLocation = CustomLocation(it.longitude, it.latitude)
                                // Update the StateFlow or LiveData with the custom location
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