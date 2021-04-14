package com.asksira.androidsampleapp.ui.search

import android.util.Log
import androidx.lifecycle.*
import com.asksira.androidsampleapp.model.WeatherRepository
import com.asksira.androidsampleapp.model.entity.RecentSearch
import com.asksira.androidsampleapp.utils.getErrorObject
import com.asksira.androidsampleapp.utils.kelvinToCelsius
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
): ViewModel() {

    val isProgressBarVisible = MutableLiveData(false)
    val isWelcomeMessageVisible = MutableLiveData(false)
    val isWeatherDataVisible = MutableLiveData(false)
    val currentCityName = MutableLiveData("")
    val minMaxTemperature = MutableLiveData<Pair<Double, Double>>()
    val humidity = MutableLiveData<Int>()
    val showErrorMessage = MutableLiveData<Pair<Boolean, String?>>() //Pair(showsOrNot, errorMessage)
    val recentSearches: LiveData<List<RecentSearch>> = weatherRepository.getRecentSearches().asLiveData()

    private var searchingKeyword: String? = null

    fun onInit() {
        viewModelScope.launch {
            val previousSearch = weatherRepository.getLatestSearchCityName()
            if (previousSearch.isNullOrBlank()) {
                isWelcomeMessageVisible.value = true
            } else {
                runSearchByKeyword(previousSearch)
            }
        }
    }

    fun onSearchKeywordConfirmed(keyword: String) {
        runSearchByKeyword(keyword)
    }

    fun hasShownErrorMessage() {
        showErrorMessage.value = Pair(false, null)
    }

    fun onErrorRetry() {
        searchingKeyword?.let { onSearchKeywordConfirmed(it) }
    }

    fun onDeleteRecentSearch(recentSearch: RecentSearch) {
        viewModelScope.launch {
            try {
                weatherRepository.deleteRecentSearch(recentSearch)
            } catch (e: Exception) {
                Log.w(TAG, "Failed to delete search record: ${e.message}")
            }
        }
    }

    private fun runSearchByKeyword(keyword: String) {
        searchingKeyword = keyword
        isProgressBarVisible.value = true
        viewModelScope.launch {
            try {
                val response = weatherRepository.getWeatherByCityName(keyword)
                val cityName = response.locationName ?: throw Exception()
                val minTemp = response.weatherData?.minTemperature?.toDouble() ?: throw Exception()
                val maxTemp = response.weatherData.maxTemperature?.toDouble() ?: throw Exception()
                val humid = response.weatherData.humidity ?: throw Exception()
                updateUI(cityName, minTemp, maxTemp, humid)
                try {
                    weatherRepository.saveSearchCityName(cityName)
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to save search record: ${e.message}")
                }
            } catch (e: Exception) {
                when (e) {
                    is HttpException -> {
                        showErrorMessage.value = Pair(true, e.getErrorObject()?.errorMessage)
                    }
                    else -> {
                        showErrorMessage.value = Pair(true, null)
                    }
                }
            } finally {
                isProgressBarVisible.value = false
            }
        }
    }

    private fun updateUI(cityName: String, minTemp: Double, maxTemp: Double, humidity: Int) {
        currentCityName.value = cityName
        minMaxTemperature.value = Pair(minTemp.kelvinToCelsius(), maxTemp.kelvinToCelsius())
        this.humidity.value = humidity
        isWeatherDataVisible.value = true
        isWelcomeMessageVisible.value = false
    }

    companion object {
        val TAG = this::class.simpleName!!
    }

}
