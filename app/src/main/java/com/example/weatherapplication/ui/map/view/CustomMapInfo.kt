package com.example.weatherapplication.ui.map.view

import android.content.Context
import android.view.LayoutInflater
import com.example.weatherapplication.R
import com.example.weatherapplication.databinding.CustomMapInfoBinding
import com.example.weatherapplication.ui.map.viewmodel.MapViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow

class CustomMapInfo(
    mapView: MapView,
    private val context: Context,
    private val mapViewModel: MapViewModel,
    private val coroutineScope: CoroutineScope
) : MarkerInfoWindow(R.layout.custom_map_info, mapView) {

    private var binding: CustomMapInfoBinding
    private val apiKey = "88be804d07441dfca3b574fec6dda8e7"

    init {
        val inflater = LayoutInflater.from(context)
        binding = CustomMapInfoBinding.inflate(inflater, mapView, false)

        mView = binding.root
    }

    override fun onOpen(item: Any?) {
        val marker = item as Marker

        binding.infoWindowIcon.setImageResource(com.example.weatherapplication.R.drawable.ic_favorites)

        binding.infoWindowIcon.setOnClickListener {
            val position = marker.position
            val lat = position.latitude
            val lon = position.longitude
            coroutineScope.launch {
                mapViewModel.fetchAndInsertCity(lat, lon, apiKey)
            }
        }
    }
}

