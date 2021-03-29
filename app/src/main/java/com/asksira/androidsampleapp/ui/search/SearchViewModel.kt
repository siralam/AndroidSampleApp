package com.asksira.androidsampleapp.ui.search

import android.util.Log
import androidx.lifecycle.*
import com.asksira.androidsampleapp.model.WeatherRepository
import com.asksira.androidsampleapp.model.entity.RecentSearch
import com.asksira.androidsampleapp.utils.kelvinToCelsius
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
): ViewModel() {

    var isProgressBarVisible = MutableLiveData(false)
        private set
    var isWelcomeMessageVisible = MutableLiveData(false)
        private set
    var isWeatherDataVisible = MutableLiveData(false)
        private set
    var currentCityName = MutableLiveData("")
        private set
    var minMaxTemperature = MutableLiveData<(Pair<Double, Double>)>()
        private set
    var humidity = MutableLiveData<Int>()
        private set
    var showsErrorMessage = MutableLiveData(false)
        private set
    var recentSearches: LiveData<List<RecentSearch>> = weatherRepository.getRecentSearches().asLiveData()
        private set

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
        showsErrorMessage.value = false
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
                showsErrorMessage.value = true
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
