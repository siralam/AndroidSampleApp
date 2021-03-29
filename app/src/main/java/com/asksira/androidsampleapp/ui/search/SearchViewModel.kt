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
