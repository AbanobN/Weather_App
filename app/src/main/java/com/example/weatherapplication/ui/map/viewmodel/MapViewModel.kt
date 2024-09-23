package com.example.weatherapplication.ui.map.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapplication.data.pojo.City
import com.example.weatherapplication.data.pojo.Coord
import com.example.weatherapplication.data.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.Locale

class MapViewModel(private val weatherRepository: WeatherRepository) : ViewModel() {

    private val _operationStatus = MutableStateFlow("")
    val operationStatus: StateFlow<String> get() = _operationStatus

    private val _cityStatus = MutableStateFlow(City("",Coord(0.0,0.0)))
    val cityStatus: StateFlow<City> get() = _cityStatus

    private val _searchFlow = MutableSharedFlow<String>(replay = 1)
    val searchFlow: SharedFlow<String> = _searchFlow

    private val countries = arrayListOf(
        "Cairo", "Alexandria", "Giza", "Shubra El-Kheima", "Port Said",
        "Suez", "Luxor", "Asyut", "Mansoura", "Tanta",
        "Ismailia", "Faiyum", "Zagazig", "Damietta", "Aswan",
        "Minya", "Beni Suef", "Qena", "Sohag", "Hurghada",
        "New York", "Los Angeles", "London", "Paris", "Tokyo",
        "Berlin", "Moscow", "Sydney", "Toronto", "Rome",
        "Mumbai", "Beijing", "Dubai", "Mexico City", "Bangkok",
        "Buenos Aires", "Istanbul", "Seoul", "Sao Paulo", "Jakarta",
        "Cape Town", "Madrid", "Vienna", "Barcelona", "Athens",
        "Lisbon", "Prague", "Warsaw", "Amsterdam", "Brussels",
        "Hong Kong", "Shanghai", "Kuala Lumpur", "Singapore", "Lagos",
        "Dublin", "Copenhagen", "Stockholm", "Helsinki", "Oslo",
        "Vancouver", "Melbourne", "Zurich", "Geneva", "Edinburgh",
        "Brisbane", "Kolkata", "Karachi", "Riyadh", "Tel Aviv",
        "Casablanca", "Manila", "Lima", "Havana", "Kyiv",
        "Nairobi", "Hanoi", "Vienna", "Budapest", "Munich",
        "Venice", "Florence", "Salvador", "Rio de Janeiro", "Lyon",
        "Marseille", "Krakow", "Copenhagen", "Montreal", "Osaka",
        "Bucharest", "Belgrade", "Sofia", "Ankara", "Tbilisi"
    )

    fun fetchAndInsertCity(lat: Double, lon: Double, apiKey: String) {
        viewModelScope.launch {
            weatherRepository.getLocationByCoordinates(lat, lon, apiKey)
                .catch { _ ->
                    _operationStatus.value = "Failure"
                }
                .collect { locationResponse ->
                    val city = City(
                        name = locationResponse.name,
                        coord = Coord(lat = locationResponse.lat, lon = locationResponse.lon)
                    )
                    weatherRepository.insertCity(city)
                    _operationStatus.value = "Done"
                }
        }
    }

    fun getLocationByName(cityName: String, apiKey: String){
        viewModelScope.launch {
            weatherRepository.getLocationByCityName(cityName,apiKey)
                .collect{ locationResponse ->
                    _cityStatus.value = City(locationResponse.name,Coord(lon = locationResponse.lon, lat = locationResponse.lat))
                }
        }
    }

    fun emitSearchQuery(query: String) {
        viewModelScope.launch {
            _searchFlow.emit(query)
        }
    }

    fun filterList(query: String): List<String> {
        return countries.filter { it.lowercase(Locale.getDefault()).startsWith(query) }
    }

}