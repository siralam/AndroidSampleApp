package com.asksira.androidsampleapp.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.asksira.androidsampleapp.R
import com.asksira.androidsampleapp.model.entity.RecentSearch

class RecentSearchAdapter: ListAdapter<RecentSearch, RecyclerView.ViewHolder>(RecentSearchDiffCallback()) {

    var onSelected: ((RecentSearch) -> Unit)? = null
    var onDeleted: ((RecentSearch) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return RecentSearchViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_recent_search, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val recentSearch = getItem(position)
        (holder as RecentSearchViewHolder).bind(recentSearch)
    }

    inner class RecentSearchViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private val tvCityName: TextView = itemView.findViewById(R.id.tvRecentSearchCityName)
        private val ivDelete: ImageView = itemView.findViewById(R.id.ivDelete)

        fun bind(item: RecentSearch) {
            tvCityName.text = item.cityName
            itemView.setOnClickListener { onSelected?.invoke(item) }
            ivDelete.setOnClickListener { onDeleted?.invoke(item) }
        }

    }

}

private class RecentSearchDiffCallback: DiffUtil.ItemCallback<RecentSearch>() {

    override fun areItemsTheSame(oldItem: RecentSearch, newItem: RecentSearch): Boolean {
        return oldItem.cityName == newItem.cityName
    }

    override fun areContentsTheSame(oldItem: RecentSearch, newItem: RecentSearch): Boolean {
        return oldItem == newItem
    }

}