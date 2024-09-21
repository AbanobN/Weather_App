package com.example.weatherapplication.ui.favorites.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapplication.databinding.FragmentFavoritesBinding
import com.example.weatherapplication.ui.favorites.viewmodel.FavoritesViewModel

class FavoritesFragment : Fragment() {

    private lateinit var fragmentFavoritesBinding: FragmentFavoritesBinding
    private lateinit var favoritesAdapter: FavoritesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val favoritesViewModel =
            ViewModelProvider(this).get(FavoritesViewModel::class.java)

        fragmentFavoritesBinding = FragmentFavoritesBinding.inflate(inflater, container, false)
        val root: View = fragmentFavoritesBinding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        favoritesAdapter = FavoritesAdapter()

        if(favoritesAdapter.currentList.isEmpty())
        {
            fragmentFavoritesBinding.recViewFavorites.visibility = View.GONE
            fragmentFavoritesBinding.noFavorites.visibility = View.VISIBLE
        }
        else
        {
            fragmentFavoritesBinding.noFavorites.visibility = View.GONE
            fragmentFavoritesBinding.recViewFavorites.apply {
                adapter = favoritesAdapter
                layoutManager = LinearLayoutManager(context)
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}