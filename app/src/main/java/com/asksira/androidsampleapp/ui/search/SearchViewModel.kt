package com.asksira.androidsampleapp.ui.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asksira.androidsampleapp.model.WeatherRepository
import kotlinx.coroutines.launch

class SearchViewModel: ViewModel() {

    private val weatherRepository = WeatherRepository()

    val isProgressBarVisible = MutableLiveData(false)
    val isWelcomeMessageVisible = MutableLiveData(true)
    val isWeatherDataVisible = MutableLiveData(false)
    val currentCityName = MutableLiveData("")
    val minMaxTemperature = MutableLiveData<(Pair<Double, Double>)>()
    val humidity = MutableLiveData<Int>()
    val showsErrorMessage = MutableLiveData(false)

    private var searchingKeyword: String? = null

    fun onSearchKeywordConfirmed(keyword: String) {
        searchingKeyword = keyword
        isProgressBarVisible.value = true

        viewModelScope.launch {
            try {
                val response = weatherRepository.getWeatherByCityName(keyword)
                isProgressBarVisible.value = false
                isWelcomeMessageVisible.value = false
                isWeatherDataVisible.value = true
                response.locationName?.let { currentCityName.value = it }
                response.weatherData?.let {
                    val min = it.minTemperature?.toDouble()
                    val max = it.maxTemperature?.toDouble()
                    if (min != null && max != null) {
                        minMaxTemperature.value = Pair(min.kelvinToCelsius(), max.kelvinToCelsius())
                    }
                    it.humidity?.let { humid -> humidity.value = humid }
                }
            } catch (e: Exception) {
                isProgressBarVisible.value = false
                showsErrorMessage.value = true
            }
        }
    }

    fun hasShownErrorMessage() {
        showsErrorMessage.value = false
    }

    fun onErrorRetry() {
        searchingKeyword?.let { onSearchKeywordConfirmed(it) }
    }

    private fun Double.kelvinToCelsius(): Double {
        return this - 273.15
    }

}
