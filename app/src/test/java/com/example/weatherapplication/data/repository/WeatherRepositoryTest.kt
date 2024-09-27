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
    fun `fetchWeather returns correct WeatherResponse`() = runBlocking {
        // Arrange
        val expectedWeatherResponse = fakeRemoteDataSource.mockWeatherResponse

        // Act
        val flow = repository.fetchWeather(30.0444, 31.2357)
        val result = flow.toList()

        // Assert
        assertEquals(1, result.size)
        assertEquals(expectedWeatherResponse, result[0])
    }

    @Test(expected = Exception::class)
    fun `fetchWeather throws exception on error`() = runBlocking{
        // Simulate an error in fetching weather data
        fakeRemoteDataSource.shouldThrowError = true

        // Act
        repository.fetchWeather(30.0444, 31.2357).collect()
    }

    @Test
    fun `fetchWeatherAndUpdate should return weather data when network is available`() = runBlocking {
        // Given
        val lat = 30.0444
        val lon = 31.2357
        val isNetwork = true

        // When
        val result = repository.fetchWeatherAndUpdate(lat, lon, isNetwork).toList()

        // Then
        assertEquals(1, result.size)
        val weatherResponse = result.first()
        assertEquals("Cairo", weatherResponse.name)
        assertEquals(25.0, weatherResponse.main.temp, 0.1)
    }

    @Test
    fun `fetchWeatherAndUpdate should return last weather data when network is not available`() = runBlocking {
        // Given
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

        // When
        val result = repository.fetchWeatherAndUpdate(lat, lon, isNetwork).toList()

        // Then
        assertEquals(1, result.size)
        val weatherResponse = result.first()
        assertEquals("Cairo", weatherResponse.name)
        assertEquals(22.0, weatherResponse.main.temp, 0.1)
    }

    @Test(expected = Exception::class)
    fun `fetchWeatherAndUpdate should throw exception when remote data source fails`() = runBlocking {
        // Given
        fakeRemoteDataSource.shouldThrowError = true
        val lat = 30.0444
        val lon = 31.2357
        val isNetwork = true

        // When
        repository.fetchWeatherAndUpdate(lat, lon, isNetwork).collect()
    }

    @Test
    fun `fetchForecast should return daily and hourly forecasts`() = runBlocking {
        // Given
        val lat = 30.0444
        val lon = 31.2357

        // When
        val result = repository.fetchForecast(lat, lon).toList()

        // Then
        assertEquals(1, result.size)
        val (hourlyForecasts, dailyForecasts) = result.first()

        // Check that daily forecasts
        assertTrue(dailyForecasts.isNotEmpty())
        assertEquals(2, dailyForecasts.size)
        assertEquals("Partly cloudy", dailyForecasts.first().weather.first().description)

        // Check
        assertTrue(hourlyForecasts.isNotEmpty())
        assertEquals(1, hourlyForecasts.size)
    }

    @Test(expected = Exception::class)
    fun `fetchForecast should throw exception when remote data source fails`() = runBlocking {
        // Given
        fakeRemoteDataSource.shouldThrowError = true
        val lat = 30.0444
        val lon = 31.2357

        // When
        repository.fetchForecast(lat, lon).collect()
    }

    @Test
    fun `fetchForecastAndUpdate with network available`() = runBlocking {
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

        assertEquals(2, dailyForecasts.size)
        assertEquals(1, hourlyForecasts.size)

        assertEquals("days", dailyForecasts[0].type)
        assertEquals("hours", hourlyForecasts[0].type)
    }

    @Test
    fun `fetchForecastAndUpdate without network available`() = runBlocking {
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

    @Test
    fun `fetchForecastAndUpdate fetches data from local on no network`() = runBlocking {
        // Arrange
        val lat = 30.0444
        val lon = 31.2357
        val isNetwork = false

        // Add mock data to the local data source
        val mockDailyForecast = ForecastItem(
            id = 1,
            dt = System.currentTimeMillis() + 86400 * 1000, // 1 day later
            main = Main(temp = 26.0, temp_min = 22.0, temp_max = 28.0, pressure = 1015, humidity = 55),
            weather = listOf(Weather(description = "Partly cloudy", icon = "02d")),
            type = "days"
        )

        val mockHourlyForecast = ForecastItem(
            id = 2,
            dt = System.currentTimeMillis() + 172800 * 1000, // 2 days later
            main = Main(temp = 24.0, temp_min = 21.0, temp_max = 26.0, pressure = 1010, humidity = 60),
            weather = listOf(Weather(description = "Rain", icon = "10d")),
            type = "hours"
        )

        fakeLocalDataSource.addForecastItem(mockDailyForecast)
        fakeLocalDataSource.addForecastItem(mockHourlyForecast)

        // Act
        val result = repository.fetchForecastAndUpdate(lat, lon, isNetwork).first()

        // Assert
        assertEquals(listOf(mockDailyForecast), result.first)  // Check daily forecasts
        assertEquals(listOf(mockHourlyForecast), result.second)  // Check hourly forecasts
    }

}
