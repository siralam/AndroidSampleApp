package com.asksira.androidsampleapp.model

import android.content.Context
import androidx.room.Room
import com.asksira.androidsampleapp.WEATHER_DB_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Provides
    @Singleton
    fun providesWeatherDatabase(@ApplicationContext appContext: Context): WeatherDatabase {
        return Room.databaseBuilder(appContext, WeatherDatabase::class.java, WEATHER_DB_NAME).build()
    }

    @Provides
    @Singleton
    fun providesRecentSearchDao(db: WeatherDatabase) = db.recentSearchDao()
}