package com.example.weatherapplication.ui.map.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
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
    private val coroutineScope: CoroutineScope,
    private val comeFrom : String,
    private val navController: NavController
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

        if(comeFrom == "Favorites")
        {
            binding.infoWindowIcon.setImageResource(com.example.weatherapplication.R.drawable.ic_favorites)
            binding.infoWindowIcon.setOnClickListener {
                val position = marker.position
                val lat = position.latitude
                val lon = position.longitude
                coroutineScope.launch {
                    mapViewModel.fetchAndInsertCity(lat, lon)
                }
            }
        }
        else{
            binding.infoWindowIcon.setImageResource(com.example.weatherapplication.R.drawable.ic_edit_location)
            binding.infoTxt.text = context.getString(R.string.up_location)
            binding.infoWindowIcon.setOnClickListener {
                val position = marker.position

                val bundle = Bundle().apply {
                    putFloat("lat", position.latitude.toFloat())
                    putFloat("lon", position.longitude.toFloat())
                }
                navController.navigate(R.id.action_mapFragment_to_nav_home,bundle)
            }
        }
    }
}

