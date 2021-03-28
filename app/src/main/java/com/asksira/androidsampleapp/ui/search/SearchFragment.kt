package com.asksira.androidsampleapp.ui.search

import android.app.SearchManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.asksira.androidsampleapp.R
import com.asksira.androidsampleapp.ui.common.ErrorRetryDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat

@AndroidEntryPoint
class SearchFragment : Fragment(), ErrorRetryDialogFragment.OnRetryListener {

    private lateinit var toolbar: Toolbar
    private lateinit var tvWelcome: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var llWeatherInfoContainer: LinearLayout
    private lateinit var tvCityName: TextView
    private lateinit var tvTemperature: TextView
    private lateinit var tvHumidity: TextView

    private val vm: SearchViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        toolbar = view.findViewById(R.id.tbSearch)
        tvWelcome = view.findViewById(R.id.tvWelcome)
        progressBar = view.findViewById(R.id.pb)
        llWeatherInfoContainer = view.findViewById(R.id.llWeatherInfoContainer)
        tvCityName = view.findViewById(R.id.tvCityName)
        tvTemperature = view.findViewById(R.id.tvTemperature)
        tvHumidity = view.findViewById(R.id.tvHumidity)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        vm.isProgressBarVisible.observe(viewLifecycleOwner) {
            progressBar.isVisible = it
        }
        vm.isWelcomeMessageVisible.observe(viewLifecycleOwner) {
            tvWelcome.isVisible = it
        }
        vm.isWeatherDataVisible.observe(viewLifecycleOwner) {
            llWeatherInfoContainer.isVisible = it
        }
        vm.currentCityName.observe(viewLifecycleOwner) {
            tvCityName.text = getString(R.string.label_city_name, it)
        }
        vm.minMaxTemperature.observe(viewLifecycleOwner) {
            val formatter = DecimalFormat("#.0")
            tvTemperature.text = getString(
                R.string.label_Temperature,
                formatter.format(it.first),
                formatter.format(it.second)
            )
        }
        vm.humidity.observe(viewLifecycleOwner) { humid ->
            tvHumidity.text = getString(R.string.label_Humidity, humid)
        }
        vm.showsErrorMessage.observe(viewLifecycleOwner) { shows ->
            if (shows) {
                val message = getString(R.string.error_get_weather)
                ErrorRetryDialogFragment().apply {
                    arguments = Bundle().apply {
                        putString("message", message)
                    }
                }.show(childFragmentManager, ErrorRetryDialogFragment.TAG)
                vm.hasShownErrorMessage()
            }
        }
    }

    override fun onErrorRetry() {
        vm.onErrorRetry()
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
                query?.let { vm.onSearchKeywordConfirmed(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })
    }



}