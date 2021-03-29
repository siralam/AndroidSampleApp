package com.asksira.androidsampleapp.ui.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asksira.androidsampleapp.model.WeatherRepository
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
    var isWelcomeMessageVisible = MutableLiveData(true)
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

    private var searchingKeyword: String? = null

    fun onSearchKeywordConfirmed(keyword: String) {
        searchingKeyword = keyword
        isProgressBarVisible.value = true

        viewModelScope.launch {
            try {
                val response = weatherRepository.getWeatherByCityName(keyword)
                val cityName = response.locationName ?: throw Exception()
                val minTemp = response.weatherData?.minTemperature?.toDouble() ?: throw Exception()
                val maxTemp = response.weatherData.maxTemperature?.toDouble() ?: throw Exception()
                val humid = response.weatherData.humidity ?: throw Exception()
                currentCityName.value = cityName
                minMaxTemperature.value = Pair(minTemp.kelvinToCelsius(), maxTemp.kelvinToCelsius())
                humidity.value = humid
                isWeatherDataVisible.value = true
                isWelcomeMessageVisible.value = false
            } catch (e: Exception) {
                showsErrorMessage.value = true
            } finally {
                isProgressBarVisible.value = false
            }
        }
    }

    fun hasShownErrorMessage() {
        showsErrorMessage.value = false
    }

    fun onErrorRetry() {
        searchingKeyword?.let { onSearchKeywordConfirmed(it) }
    }

}
