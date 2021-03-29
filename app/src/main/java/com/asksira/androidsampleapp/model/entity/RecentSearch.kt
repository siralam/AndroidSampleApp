package com.asksira.androidsampleapp.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Declaring the column info allows for the renaming of variables without implementing a
 * database migration, as the column name would not change.
 */
@Entity
data class RecentSearch (

    @PrimaryKey
    @ColumnInfo(name = "city_name")
    val cityName: String,

    @ColumnInfo(name = "search_time", index = true)
    val searchTime: Long
)