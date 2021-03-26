package com.asksira.androidsampleapp.main.search

import android.app.SearchManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.asksira.androidsampleapp.R

class SearchFragment: Fragment() {

    private lateinit var toolbar: Toolbar
    private lateinit var tvWelcome: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvCityName: TextView
    private lateinit var tvTemperature: TextView
    private lateinit var tvHumidity: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        toolbar = view.findViewById(R.id.tbSearch)
        tvWelcome = view.findViewById(R.id.tvWelcome)
        progressBar = view.findViewById(R.id.pb)
        tvCityName = view.findViewById(R.id.tvCityName)
        tvTemperature = view.findViewById(R.id.tvTemperature)
        tvHumidity = view.findViewById(R.id.tvHumidity)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
    }

    private fun setupToolbar() {
        val context = context ?: return
        toolbar.inflateMenu(R.menu.search_options_menu)
        val menuItem = toolbar.menu.findItem(R.id.search)
        val searchManager = getSystemService(context, SearchManager::class.java)
        val searchView = menuItem.actionView as? SearchView ?: return
        searchView.setSearchableInfo(searchManager?.getSearchableInfo(activity?.componentName))
        searchView.isSubmitButtonEnabled = true
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Toast.makeText(context, query, Toast.LENGTH_SHORT).show()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })
    }

}