package com.example.weatherapplication.ui.home.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.weatherapplication.data.pojo.Clouds
import com.example.weatherapplication.data.pojo.Coord
import com.example.weatherapplication.data.pojo.ForecastItem
import com.example.weatherapplication.data.pojo.Main
import com.example.weatherapplication.data.pojo.Weather
import com.example.weatherapplication.data.pojo.WeatherResponse
import com.example.weatherapplication.data.pojo.Wind
import com.example.weatherapplication.data.repository.IWeatherRepository
import com.example.weatherapplication.utiltes.ForecastApiState
import com.example.weatherapplication.utiltes.InternetState
import com.example.weatherapplication.utiltes.WeatherApiState
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
    fun `fetchWeatherData updates weatherApiState`() = runTest {
        // Given
        val weatherResponse = WeatherResponse(
            main = Main(temp = 25.0, temp_min = 20.0, temp_max = 30.0, pressure = 1013, humidity = 50),
            weather = listOf(Weather(description = "Clear sky", icon = "01d")),
            visibility = 10000,
            wind = Wind(speed = 5.0),
            clouds = Clouds(all = 0),
            dt = System.currentTimeMillis(),
            timezone = 3600,
            coord = Coord(lon = 31.2357, lat = 30.0444),
            name = "Cairo"
        )

        // Mocking the repository to return a flow with weatherResponse
        `when`(weatherRepository.fetchWeatherAndUpdate(anyDouble(), anyDouble(), anyBoolean()))
            .thenReturn(flow { emit(weatherResponse) })

        // Testing the flow of states using Turbine
        homeViewModel.weatherApiState.test {
            // When
            homeViewModel.fetchWeatherData(0.0, 0.0, true)

            // Then: Expecting Loading state first
            assertEquals(WeatherApiState.Loading, awaitItem())

            // Then: Expecting Success state with the correct weatherResponse
            val successState = awaitItem() as WeatherApiState.Success
            assertEquals(weatherResponse, successState.weatherResponse)

            // Ensure no further items are emitted
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `fetchForecastData updates forecastApiState`() = runTest {
        // Given
        val dailyForecasts = listOf(
            ForecastItem(
                id = 1,
                dt = System.currentTimeMillis() + 86400 * 1000, // 1 day later
                main = Main(temp = 26.0, temp_min = 22.0, temp_max = 28.0, pressure = 1015, humidity = 55),
                weather = listOf(Weather(description = "Partly cloudy", icon = "02d")),
                type = "days"
            )
        )
        val hourlyForecasts = listOf(
            ForecastItem(
                id = 2,
                dt = System.currentTimeMillis() + 172800 * 1000, // 2 days later
                main = Main(temp = 24.0, temp_min = 21.0, temp_max = 26.0, pressure = 1010, humidity = 60),
                weather = listOf(Weather(description = "Rain", icon = "10d")),
                type = "hours"
            )
        )

        // Mocking the weather repository to return the forecasts
        `when`(weatherRepository.fetchForecastAndUpdate(anyDouble(), anyDouble(), anyBoolean()))
            .thenReturn(flow { emit(Pair(dailyForecasts, hourlyForecasts)) })

        // Testing the flow of states using Turbine
        homeViewModel.forecastApiState.test {
            // When
            homeViewModel.fetchForecastData(0.0, 0.0, true)

            // Then: Expecting Loading state first
            assertEquals(ForecastApiState.Loading, awaitItem())

            // Then: Expecting Success state with the correct forecasts
            val successState = awaitItem() as ForecastApiState.Success
            assertEquals(dailyForecasts, successState.dailyForecasts)
            assertEquals(hourlyForecasts, successState.hourlyForecasts)

            // Ensure no further items are emitted
            cancelAndIgnoreRemainingEvents()
        }
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
        Dispatchers.resetMain() // Reset main dispatcher
    }


}
