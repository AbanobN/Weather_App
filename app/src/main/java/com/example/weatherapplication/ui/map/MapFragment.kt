package com.example.weatherapplication.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.weatherapplication.R
import com.example.weatherapplication.data.localdatasource.database.AppDatabase
import com.example.weatherapplication.data.localdatasource.localdatsource.LocalDataSource
import com.example.weatherapplication.data.remotedatasource.remotedatasource.RemoteDataSource
import com.example.weatherapplication.data.repository.WeatherRepository
import com.example.weatherapplication.databinding.FragmentMapBinding
import com.example.weatherapplication.ui.home.viewmodel.HomeViewModel
import com.example.weatherapplication.ui.home.viewmodel.HomeViewModelFactory
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.library.BuildConfig
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

class MapFragment : Fragment() {

    private lateinit var binding: FragmentMapBinding
    private lateinit var mapViewModel: MapViewModel
    private var marker: Marker? = null

    var lat :Double=0.0
    var lon:Double=0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val remoteDataSource = RemoteDataSource()
        val localDataSource = LocalDataSource(AppDatabase.getDatabase(requireContext()))
        mapViewModel = ViewModelProvider(this, MapViewModelFactory(WeatherRepository(remoteDataSource,localDataSource))).get(
            MapViewModel::class.java)

        binding= FragmentMapBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            mapViewModel.operationStatus.collect{ state ->
                when(state)
                {
                    "Done" -> findNavController().navigate(R.id.action_mapFragment_to_nav_favorites)
                    "Failure" -> Toast.makeText(context, "Failed to add city.", Toast.LENGTH_SHORT).show()
                }

            }
        }


        // Initialize map
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
        binding.map.setTileSource(TileSourceFactory.MAPNIK)
        binding.map.setMultiTouchControls(true)
        binding.map.controller.setZoom(15.0)
        val startPoint = GeoPoint(31.1843, 29.92)
        binding.map.controller.setCenter(startPoint)

        // Add event listener for map taps
        val mapEventsReceiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                p?.let {
                    updateMarkerPosition(it)
                    lat=it.latitude
                    lon=it.longitude
                }
                return true
            }

            override fun longPressHelper(p: GeoPoint?): Boolean {
                return false
            }
        }

        val overlayEvents = MapEventsOverlay(mapEventsReceiver)
        binding.map.overlays.add(overlayEvents)

    }

    private fun updateMarkerPosition(location: GeoPoint) {
        if (marker == null) {
            marker = Marker(binding.map).apply {
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

                val customMarkerIcon = ContextCompat.getDrawable(requireContext(), R.drawable.map_marker)
                icon = customMarkerIcon

                val customInfoWindow = CustomMapInfo(binding.map, requireContext(), mapViewModel,lifecycleScope)
                infoWindow = customInfoWindow

                setOnMarkerClickListener { m, _ ->
                    m.closeInfoWindow()
                    true
                }

                binding.map.overlays.add(this)
            }
        }

        marker?.apply {
            position = location
            showInfoWindow()
        }


        binding.map.invalidate()
    }


    override fun onResume() {
        super.onResume()
        binding.map.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.map.onPause()
    }

}