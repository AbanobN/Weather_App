package com.example.weatherapplication.ui.favorites.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapplication.data.localdatasource.database.AppDatabase
import com.example.weatherapplication.data.localdatasource.localdatsource.LocalDataSource
import com.example.weatherapplication.data.remotedatasource.remotedatasource.RemoteDataSource
import com.example.weatherapplication.data.repository.WeatherRepository
import com.example.weatherapplication.databinding.FragmentFavoritesBinding
import com.example.weatherapplication.ui.favorites.viewmodel.FavoritesViewModel
import com.example.weatherapplication.ui.favorites.viewmodel.FavoritesViewModelFactory

class FavoritesFragment : Fragment() {

    private lateinit var fragmentFavoritesBinding: FragmentFavoritesBinding
    private lateinit var favoritesAdapter: FavoritesAdapter
    private lateinit var favoritesViewModel:FavoritesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val remoteDataSource = RemoteDataSource()
        val localDataSource = LocalDataSource(AppDatabase.getDatabase(requireContext()))

        favoritesViewModel =
            ViewModelProvider(this, FavoritesViewModelFactory(WeatherRepository(remoteDataSource,localDataSource))).get(FavoritesViewModel::class.java)

        favoritesViewModel.fetchFavorites()

        fragmentFavoritesBinding = FragmentFavoritesBinding.inflate(inflater, container, false)
        val root: View = fragmentFavoritesBinding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favoritesAdapter = FavoritesAdapter()
        fragmentFavoritesBinding.recViewFavorites.apply {
            adapter = favoritesAdapter
            layoutManager = LinearLayoutManager(context)
        }

        favoritesViewModel.favoritesLiveData.observe(viewLifecycleOwner) { favorites ->
            favoritesAdapter.submitList(favorites)
            showHideRecView()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    private fun showHideRecView()
    {
        if(favoritesAdapter.currentList.isEmpty())
        {
            fragmentFavoritesBinding.recViewFavorites.visibility = View.GONE
            fragmentFavoritesBinding.noFavorites.visibility = View.VISIBLE
        }
        else
        {
            fragmentFavoritesBinding.noFavorites.visibility = View.GONE

        }
    }
}