package com.asksira.androidsampleapp.ui.search

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.asksira.androidsampleapp.model.WeatherRepository
import com.asksira.androidsampleapp.model.entity.RecentSearch
import com.asksira.androidsampleapp.network.dto.WeatherData
import com.asksira.androidsampleapp.network.dto.WeatherResponse
import com.asksira.androidsampleapp.ui.search.testutil.MainCoroutineRule
import com.google.common.truth.Truth.assertThat
import io.mockk.*
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

    private val searchCityName = "Hong Kong"

    @Before
    fun setup() {
        vm = SearchViewModel(weatherRepository)
    }

    @Test
    fun `onSearchKeyword calls API and did updates the LiveData`() {
        mockWeatherResponse(searchCityName)
        runBlocking {
            vm.onSearchKeywordConfirmed(searchCityName)
        }
        assertThat(vm.currentCityName.value).matches(searchCityName)
        assertThat(vm.minMaxTemperature.value).isEqualTo(Pair(17.0, 27.0))
        assertThat(vm.humidity.value).isEqualTo(80)
    }

    @Test
    fun `onSearchKeyword calls API but failed, try to show error message`() {
        mockWeatherError()
        runBlocking {
            vm.onSearchKeywordConfirmed(searchCityName)
        }
        assertThat(vm.showErrorMessage.value?.first).isTrue()
    }

    @Test
    fun `If server returns unexpected format, try to show error message`() {
        coEvery {
            weatherRepository.getWeatherByCityName(any())
        } returns WeatherResponse(null, null)
        runBlocking {
            vm.onSearchKeywordConfirmed(searchCityName)
        }
        assertThat(vm.showErrorMessage.value?.first).isTrue()
    }

    @Test
    fun `When user retries, executes the search again`() {
        mockWeatherError()
        runBlocking {
            vm.onSearchKeywordConfirmed(searchCityName)
        }
        mockWeatherResponse(searchCityName)
        vm.onErrorRetry()
        assertThat(vm.currentCityName.value).matches(searchCityName)
        assertThat(vm.minMaxTemperature.value).isEqualTo(Pair(17.0, 27.0))
        assertThat(vm.humidity.value).isEqualTo(80)
    }

    @Test
    fun `When user searches using a city name, it is trying to save to local DB`() {
        mockWeatherResponse(searchCityName)
        mockSaveSearchRecordToDBSuccess()
        vm.onSearchKeywordConfirmed(searchCityName)
        coVerify {
            weatherRepository.saveSearchCityName(any())
        }
    }

    @Test
    fun `When user deletes a recent search, it is trying to remove from local DB`() {
        mockDeleteSearchRecordSuccess()
        vm.onDeleteRecentSearch(RecentSearch(searchCityName, System.currentTimeMillis()))
        coVerify {
            weatherRepository.deleteRecentSearch(any())
        }
    }

    private fun mockWeatherResponse(cityName: String) {
        coEvery {
            weatherRepository.getWeatherByCityName(cityName)
        } returns WeatherResponse(
            locationName = cityName,
            weatherData = WeatherData(
                temperature = 293.0,
                feelsLikeTemperature = 295.0,
                minTemperature = 290.15,
                maxTemperature = 300.15,
                pressure = 1009,
                humidity = 80
            )
        )
    }

    private fun mockWeatherError() {
        coEvery {
            weatherRepository.getWeatherByCityName(any())
        } throws IOException()
    }

    private fun mockSaveSearchRecordToDBSuccess() {
        coEvery {
            weatherRepository.saveSearchCityName(any())
        } just runs
    }

    private fun mockDeleteSearchRecordSuccess() {
        coEvery {
            weatherRepository.deleteRecentSearch(any())
        } just runs
    }
}