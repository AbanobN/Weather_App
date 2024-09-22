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

    private val _searchFlow = MutableSharedFlow<String>(replay = 1)
    val searchFlow: SharedFlow<String> = _searchFlow

    private val countries = arrayListOf(

        "Afghanistan", "Albania", "Algeria", "Andorra", "Angola",
        "Antigua and Barbuda", "Argentina", "Armenia", "Australia", "Austria",
        "Azerbaijan", "Bahamas", "Bahrain", "Bangladesh", "Barbados",
        "Belarus", "Belgium", "Belize", "Benin", "Bhutan",
        "Bolivia", "Bosnia and Herzegovina", "Botswana", "Brazil", "Brunei",
        "Bulgaria", "Burkina Faso", "Burundi", "Cabo Verde", "Cambodia",
        "Cameroon", "Canada", "Central African Republic", "Chad", "Chile",
        "China", "Colombia", "Comoros", "Congo", "Costa Rica",
        "Cuba", "Croatia", "Cyprus", "Czech Republic", "Denmark",

        "Cairo", "Alexandria", "Giza", "Shubra El-Kheima", "Port Said",
        "Suez", "Luxor", "Asyut", "Mansoura", "Tanta",
        "Ismailia", "Faiyum", "Zagazig", "Damietta", "Aswan",
        "Minya", "Beni Suef", "Qena", "Sohag", "Hurghada",

        "Djibouti", "Dominica", "Dominican Republic", "Ecuador", "Egypt",
        "El Salvador", "Equatorial Guinea", "Eritrea", "Estonia", "Eswatini",
        "Ethiopia", "Fiji", "Finland", "France", "Gabon",
        "Gambia", "Georgia", "Germany", "Ghana", "Greece",
        "Grenada", "Guatemala", "Guinea", "Guinea-Bissau", "Guyana",
        "Haiti", "Honduras", "Hungary", "Iceland", "India"
    )

    fun fetchAndInsertCity(lat: Double, lon: Double, apiKey: String) {
        viewModelScope.launch {
            weatherRepository.getLocationByCoordinates(lat, lon, apiKey)
                .catch { _ ->
                    _operationStatus.value = "Failure"
                }
                .collect { nameResponse ->
                    val city = City(
                        name = nameResponse.name,
                        coord = Coord(lat = lat, lon = lon)
                    )
                    weatherRepository.insertCity(city)
                    _operationStatus.value = "Done"
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