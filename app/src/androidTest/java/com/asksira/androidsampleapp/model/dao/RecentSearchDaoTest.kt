package com.asksira.androidsampleapp.model.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.asksira.androidsampleapp.model.WeatherDatabase
import com.asksira.androidsampleapp.testRecentSearch
import com.asksira.androidsampleapp.testRecentSearches
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class RecentSearchDaoTest {
    private lateinit var database: WeatherDatabase
    private lateinit var recentSearchDao: RecentSearchDao

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun createDb() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, WeatherDatabase::class.java).build()
        recentSearchDao = database.recentSearchDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }

    @Test
    fun testAddOneRecentSearch() {
        runBlocking {
            recentSearchDao.save(testRecentSearch)
            val inserted = recentSearchDao.getAll().first()
            val theOne = inserted[0]
            assertThat(theOne.cityName == testRecentSearch.cityName)
        }
    }

    @Test
    fun testGetLatestRecentSearch() {
        runBlocking {
            val subjects = testRecentSearches
            for (each in subjects) {
                recentSearchDao.save(each)
            }
            val expectedSubject = subjects.maxOf { it.searchTime }
            assertThat(expectedSubject == recentSearchDao.getLatest()!!.searchTime)
        }
    }

    @Test
    fun testDeleteRecentSearch() {
        runBlocking {
            val subject = testRecentSearch
            recentSearchDao.save(subject)
            recentSearchDao.delete(subject)
            assertThat(recentSearchDao.getAll().first().isEmpty())
        }
    }

}
