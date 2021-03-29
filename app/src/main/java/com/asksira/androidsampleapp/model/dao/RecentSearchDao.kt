package com.asksira.androidsampleapp.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.asksira.androidsampleapp.model.entity.RecentSearch
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentSearchDao {

    @Insert(onConflict = REPLACE)
    suspend fun save(recentSearch: RecentSearch)

    @Query("SELECT * FROM recentSearch")
    fun getAll(): Flow<List<RecentSearch>>

    @Query("SELECT * FROM recentSearch ORDER BY search_time DESC LIMIT 1")
    suspend fun getLatest(): RecentSearch?

    @Delete
    suspend fun delete(recentSearch: RecentSearch)
}