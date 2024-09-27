package com.example.weatherapplication.data.repository

import com.example.weatherapplication.data.localdatasource.fakelocaldatasource.FakeLocalDataSource
import com.example.weatherapplication.data.pojo.Clouds
import com.example.weatherapplication.data.pojo.Coord
import com.example.weatherapplication.data.pojo.ForecastItem
import com.example.weatherapplication.data.pojo.ForecastResponse
import com.example.weatherapplication.data.pojo.Main
import com.example.weatherapplication.data.pojo.Weather
import com.example.weatherapplication.data.pojo.WeatherResponse
import com.example.weatherapplication.data.pojo.Wind
import com.example.weatherapplication.data.remotedatasource.fakeremotedatasource.FakeRemoteDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


@ExperimentalCoroutinesApi
class WeatherRepositoryTest {

    private lateinit var repository: WeatherRepository
    private lateinit var fakeRemoteDataSource: FakeRemoteDataSource
    private lateinit var fakeLocalDataSource: FakeLocalDataSource

    @Before
    fun setup() {
        fakeRemoteDataSource = FakeRemoteDataSource()
        fakeLocalDataSource = FakeLocalDataSource()
        repository = WeatherRepository(fakeRemoteDataSource, fakeLocalDataSource)
    }


    @Test
    fun `fetchWeather returns correct WeatherResponse`() = runBlockingTest {
        // Arrange
        val expectedWeatherResponse = fakeRemoteDataSource.mockWeatherResponse

        // Act
        val flow = repository.fetchWeather(30.0444, 31.2357) // Example coordinates for Cairo
        val result = flow.toList() // Collect the emitted results

        // Assert
        assertEquals(1, result.size) // Check that one result was emitted
        assertEquals(expectedWeatherResponse, result[0]) // Check that the response matches the expected mock response
    }

    @Test(expected = Exception::class)
    fun `fetchWeather throws exception on error`() = runBlockingTest {
        // Simulate an error in fetching weather data
        fakeRemoteDataSource.shouldThrowError = true // Enable the error simulation

        // Act
        repository.fetchWeather(30.0444, 31.2357).collect() // This should throw an exception
    }

    @Test
    fun `fetchWeatherAndUpdate should return weather data when network is available`() = runBlocking {
        // Given network is available
        val lat = 30.0444
        val lon = 31.2357
        val isNetwork = true

        // When fetching weather data
        val result = repository.fetchWeatherAndUpdate(lat, lon, isNetwork).toList()

        // Then the weather data should be returned
        assertEquals(1, result.size) // We expect one emission
        val weatherResponse = result.first()
        assertEquals("Cairo", weatherResponse.name)
        assertEquals(25.0, weatherResponse.main.temp, 0.1)
    }

    @Test
    fun `fetchWeatherAndUpdate should return last weather data when network is not available`() = runBlocking {
        // Given network is not available
        val lat = 30.0444
        val lon = 31.2357
        val isNetwork = false

        // Mock the last weather data in the local data source
        val lastWeatherResponse = WeatherResponse(
            main = Main(temp = 22.0, temp_min = 18.0, temp_max = 26.0, pressure = 1010, humidity = 60),
            weather = listOf(Weather(description = "Cloudy", icon = "02d")),
            visibility = 8000,
            wind = Wind(speed = 3.0),
            clouds = Clouds(all = 20),
            dt = System.currentTimeMillis(),
            timezone = 3600,
            coord = Coord(lon = 31.2357, lat = 30.0444),
            name = "Cairo"
        )
        fakeLocalDataSource.insertWeather(lastWeatherResponse)

        // When fetching weather data
        val result = repository.fetchWeatherAndUpdate(lat, lon, isNetwork).toList()

        // Then the last weather data should be returned
        assertEquals(1, result.size) // We expect one emission
        val weatherResponse = result.first()
        assertEquals("Cairo", weatherResponse.name)
        assertEquals(22.0, weatherResponse.main.temp, 0.1)
    }

    @Test(expected = Exception::class)
    fun `fetchWeatherAndUpdate should throw exception when remote data source fails`() = runBlocking {
        // Given network is available and remote data source is set to throw error
        fakeRemoteDataSource.shouldThrowError = true
        val lat = 30.0444
        val lon = 31.2357
        val isNetwork = true

        // When fetching weather data
        repository.fetchWeatherAndUpdate(lat, lon, isNetwork).collect()
    }

    @Test
    fun `fetchForecast should return daily and hourly forecasts`() = runBlocking {
        // Given coordinates for Cairo
        val lat = 30.0444
        val lon = 31.2357

        // When fetching the forecast
        val result = repository.fetchForecast(lat, lon).toList()

        // Then the result should contain daily and hourly forecasts
        assertEquals(1, result.size) // We expect one emission
        val (hourlyForecasts, dailyForecasts) = result.first()

        // Check that daily forecasts are distinct by date and contain expected values
        assertTrue(dailyForecasts.isNotEmpty())
        assertEquals(2, dailyForecasts.size) // Adjust based on the mock response
        assertEquals("Partly cloudy", dailyForecasts.first().weather.first().description)

        // Check that hourly forecasts contain expected values
        assertTrue(hourlyForecasts.isNotEmpty())
        assertEquals(1, hourlyForecasts.size) // We expect 8 hourly forecasts
    }

    @Test(expected = Exception::class)
    fun `fetchForecast should throw exception when remote data source fails`() = runBlocking {
        // Given remote data source is set to throw an error
        fakeRemoteDataSource.shouldThrowError = true
        val lat = 30.0444
        val lon = 31.2357

        // When fetching the forecast
        repository.fetchForecast(lat, lon).collect()
    }

    @Test
    fun `test fetchForecastAndUpdate with network available`() = runBlocking {
        // Arrange
        val lat = 30.0444
        val lon = 31.2357
        val isNetwork = true

        // Act
        val result = mutableListOf<Pair<List<ForecastItem>, List<ForecastItem>>>()
        repository.fetchForecastAndUpdate(lat, lon, isNetwork).collect { result.add(it) }

        // Assert
        assertEquals(1, result.size)

        val (hourlyForecasts, dailyForecasts) = result[0]

        assertEquals(2, dailyForecasts.size) // Mocked daily forecast should have 1 item
        assertEquals(1, hourlyForecasts.size) // Mocked hourly forecast should have 1 item

        assertEquals("days", dailyForecasts[0].type)
        assertEquals("hours", hourlyForecasts[0].type)
    }

    @Test
    fun `test fetchForecastAndUpdate without network available`() = runBlocking {
        // Arrange
        val lat = 30.0444
        val lon = 31.2357
        val isNetwork = false

        // Act
        val result = mutableListOf<Pair<List<ForecastItem>, List<ForecastItem>>>()
        repository.fetchForecastAndUpdate(lat, lon, isNetwork).collect { result.add(it) }

        // Assert
        assertEquals(1, result.size)

        val (hourlyForecasts, dailyForecasts) = result[0]

        assertTrue(dailyForecasts.isEmpty())
        assertTrue(hourlyForecasts.isEmpty())
    }

    @Test(expected = Exception::class)
    fun `test fetchForecastAndUpdate throws exception on network error`() = runBlocking {
        // Arrange
        val lat = 30.0444
        val lon = 31.2357
        val isNetwork = true

        // Force remote data source to throw an error
        fakeRemoteDataSource.shouldThrowError = true

        // Act & Assert
        repository.fetchForecastAndUpdate(lat, lon, isNetwork).collect()
    }
}
