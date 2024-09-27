package com.example.weatherapplication.ui.home.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.weatherapplication.data.pojo.Clouds
import com.example.weatherapplication.data.pojo.Coord
import com.example.weatherapplication.data.pojo.ForecastItem
import com.example.weatherapplication.data.pojo.Main
import com.example.weatherapplication.data.pojo.Weather
import com.example.weatherapplication.data.pojo.WeatherResponse
import com.example.weatherapplication.data.pojo.Wind
import com.example.weatherapplication.data.repository.IWeatherRepository
import com.example.weatherapplication.utiltes.InternetState
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.mockito.ArgumentMatchers.anyBoolean
import org.mockito.ArgumentMatchers.anyDouble
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import kotlin.test.Test


@ExperimentalCoroutinesApi
class HomeViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var weatherRepository: IWeatherRepository
    private lateinit var internetState: InternetState
    private lateinit var homeViewModel: HomeViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        weatherRepository = mock(IWeatherRepository::class.java)
        internetState = mock(InternetState::class.java)
        homeViewModel = HomeViewModel(weatherRepository, internetState)
    }

    @Test
    fun `fetchWeatherData updates weatherData`() = runTest {
        // Given
        val weatherResponse = WeatherResponse(main = Main(temp = 25.0, temp_min = 20.0, temp_max = 30.0, pressure = 1013, humidity = 50),
            weather = listOf(Weather(description = "Clear sky", icon = "01d")),
            visibility = 10000,
            wind = Wind(speed = 5.0),
            clouds = Clouds(all = 0),
            dt = System.currentTimeMillis(),
            timezone = 3600,
            coord = Coord(lon = 31.2357, lat = 30.0444),
            name = "Cairo")
        `when`(weatherRepository.fetchWeatherAndUpdate(anyDouble(), anyDouble(), anyBoolean()))
            .thenReturn(flow { emit(weatherResponse) })

        // When
        homeViewModel.fetchWeatherData(0.0, 0.0, true)

        // Then
        assertEquals(weatherResponse, homeViewModel.weatherData.value)
    }

    @Test
    fun `fetchForecastData updates days and hours`() = runTest {
        // Given
        val dailyForecasts = listOf(ForecastItem( id = 1,
            dt = System.currentTimeMillis() + 86400 * 1000, // 1 day later
            main = Main(temp = 26.0, temp_min = 22.0, temp_max = 28.0, pressure = 1015, humidity = 55),
            weather = listOf(Weather(description = "Partly cloudy", icon = "02d")),
            type = "days"
        ))
        val hourlyForecasts = listOf(ForecastItem(id = 2,
            dt = System.currentTimeMillis() + 172800 * 1000, // 2 days later
            main = Main(temp = 24.0, temp_min = 21.0, temp_max = 26.0, pressure = 1010, humidity = 60),
            weather = listOf(Weather(description = "Rain", icon = "10d")),
            type = "hours"))
        `when`(weatherRepository.fetchForecastAndUpdate(anyDouble(), anyDouble(), anyBoolean()))
            .thenReturn(flow { emit(Pair(dailyForecasts, hourlyForecasts)) })

        // When
        homeViewModel.fetchForecastData(0.0, 0.0, true)

        // Then
        assertEquals(dailyForecasts, homeViewModel.days.value)
        assertEquals(hourlyForecasts, homeViewModel.hours.value)
    }

    @Test
    fun `updateSettings updates tempUnit and windSpeed`() {
        // Given
        `when`(weatherRepository.getUnit()).thenReturn("C")
        `when`(weatherRepository.getSpeed()).thenReturn("mph")

        // When
        homeViewModel.updateSettings()

        // Then
        assertEquals("C", homeViewModel.tempUnit.value)
        assertEquals("mph", homeViewModel.windSpeed.value)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // Reset main dispatcher for tests
    }


}
