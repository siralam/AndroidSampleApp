package com.asksira.androidsampleapp.model

import androidx.room.Database
import androidx.room.RoomDatabase
import com.asksira.androidsampleapp.model.dao.RecentSearchDao
import com.asksira.androidsampleapp.model.entity.RecentSearch

@Database(entities = [RecentSearch::class], version = 1)
abstract class WeatherDatabase: RoomDatabase() {
    abstract fun recentSearchDao(): RecentSearchDao
}