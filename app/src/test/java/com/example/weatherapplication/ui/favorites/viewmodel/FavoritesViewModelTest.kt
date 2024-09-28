package com.example.weatherapplication.ui.favorites.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.weatherapplication.data.pojo.City
import com.example.weatherapplication.data.pojo.Coord
import com.example.weatherapplication.data.repository.IWeatherRepository
import com.example.weatherapplication.utiltes.InternetState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*

@ExperimentalCoroutinesApi
class FavoritesViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var weatherRepository: IWeatherRepository
    private lateinit var favoritesViewModel: FavoritesViewModel
    private lateinit var internetState: InternetState

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        weatherRepository = mock(IWeatherRepository::class.java)
        internetState = mock(InternetState::class.java)
        favoritesViewModel = FavoritesViewModel(weatherRepository, internetState)
    }

    @Test
    fun `fetchFavorites updates favoritesList`() = runTest {
        // Given
        val cityList = listOf(
            City("Cairo", Coord(lon = 31.2357, lat = 30.0444)),
            City("Alexandria",Coord(lon = 31.2357, lat = 30.0444))
        )
        `when`(weatherRepository.getAllCities()).thenReturn(flow { emit(cityList) })

        // When
        favoritesViewModel.fetchFavorites()

        // Then
        assertEquals(cityList, favoritesViewModel.favoritesList.value)
    }

    @Test
    fun `removeFavorite calls deleteCity on repository`() = runTest {
        // Given
        val cityName = "Cairo"

        // When
        favoritesViewModel.removeFavorite(cityName)

        // Then
        verify(weatherRepository).deleteCity(cityName)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // Reset main dispatcher
    }
}