package com.asksira.androidsampleapp.ui.search

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.asksira.androidsampleapp.model.WeatherRepository
import com.asksira.androidsampleapp.network.dto.WeatherData
import com.asksira.androidsampleapp.network.dto.WeatherResponse
import com.asksira.androidsampleapp.ui.search.testutil.MainCoroutineRule
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException


class SearchViewModelTest {
    private lateinit var vm: SearchViewModel

    /**
     * This is needed in order for LiveData's update to occur synchronously.
     */
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private val weatherRepository = mockk<WeatherRepository>(relaxed = true)

    private val mockWeatherResponse = WeatherResponse(
        locationName = "Hong Kong",
        weatherData = WeatherData(
            temperature = 293.0,
            feelsLikeTemperature = 295.0,
            minTemperature = 290.15,
            maxTemperature = 300.15,
            pressure = 1009,
            humidity = 80
        )
    )

    @Before
    fun setup() {
        vm = SearchViewModel(weatherRepository)
    }

    @Test
    fun `onSearchKeyword calls API and did updates the UI`() {
        mockWeatherResponse()
        runBlocking {
            vm.onSearchKeywordConfirmed("Hong Kong")
        }
        assertThat(vm.currentCityName.value).matches("Hong Kong")
        assertThat(vm.minMaxTemperature.value).isEqualTo(Pair(17.0, 27.0))
        assertThat(vm.humidity.value).isEqualTo(80)
    }

    @Test
    fun `onSearchKeyword calls API but failed, shows Error Message`() {
        mockWeatherError()
        runBlocking {
            vm.onSearchKeywordConfirmed("Hong Kong")
        }
        assertThat(vm.showsErrorMessage.value).isTrue()
    }

    @Test
    fun `If server returns unexpected format, shows Error Message`() {
        coEvery {
            weatherRepository.getWeatherByCityName(any())
        } returns WeatherResponse(null, null)
        runBlocking {
            vm.onSearchKeywordConfirmed("Hong Kong")
        }
        assertThat(vm.showsErrorMessage.value).isTrue()
    }

    @Test
    fun `When user clicks retry, VM executes the search again`() {
        val keyword = "Hong Kong"
        mockWeatherError()
        runBlocking {
            vm.onSearchKeywordConfirmed(keyword)
        }
        mockWeatherResponse()
        vm.onErrorRetry()
        assertThat(vm.currentCityName.value).matches("Hong Kong")
        assertThat(vm.minMaxTemperature.value).isEqualTo(Pair(17.0, 27.0))
        assertThat(vm.humidity.value).isEqualTo(80)
    }

    private fun mockWeatherResponse() {
        coEvery {
            weatherRepository.getWeatherByCityName(any())
        } returns mockWeatherResponse
    }

    private fun mockWeatherError() {
        coEvery {
            weatherRepository.getWeatherByCityName(any())
        } throws IOException()
    }
}