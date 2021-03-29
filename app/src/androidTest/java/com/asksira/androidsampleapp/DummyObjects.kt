package com.asksira.androidsampleapp

import com.asksira.androidsampleapp.model.entity.RecentSearch

val testRecentSearch = RecentSearch("Hong Kong", System.currentTimeMillis())

val testRecentSearches = arrayListOf(
    RecentSearch("Hong Kong", System.currentTimeMillis()),
    RecentSearch("Taipei", System.currentTimeMillis() - 5000),
    RecentSearch("New York", System.currentTimeMillis() - 60000) ,
)
