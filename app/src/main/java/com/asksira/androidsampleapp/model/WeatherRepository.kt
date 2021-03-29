package com.asksira.androidsampleapp.model

import com.asksira.androidsampleapp.model.dao.RecentSearchDao
import com.asksira.androidsampleapp.model.entity.RecentSearch
import com.asksira.androidsampleapp.network.OpenWeatherService
import com.asksira.androidsampleapp.network.dto.WeatherResponse
import com.asksira.androidsampleapp.utils.di.ApiKey
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val weatherService: OpenWeatherService,
    @ApiKey private val apiKey: String,
    private val recentSearchDao: RecentSearchDao
) {

    suspend fun getWeatherByCityName(cityName: String): WeatherResponse {
        return weatherService.getWeatherByCityName(cityName, apiKey)
    }

    fun getRecentSearches() = recentSearchDao.getAll().map { list ->
        list.sortedByDescending { it.searchTime }
    }

    suspend fun getLatestSearchCityName() = recentSearchDao.getLatest()?.cityName

    suspend fun saveSearchCityName(cityName: String) {
        recentSearchDao.save(RecentSearch(cityName, System.currentTimeMillis()))
    }

    suspend fun deleteRecentSearch(recentSearch: RecentSearch) {
        recentSearchDao.delete(recentSearch)
    }

}