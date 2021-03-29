package com.asksira.androidsampleapp.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.asksira.androidsampleapp.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecentSearchBottomSheetDialogFragment: BottomSheetDialogFragment() {

    private lateinit var rv: RecyclerView

    private val adapter = RecentSearchAdapter().apply {
        onSelected = { recentSearch ->
            vm.onSearchKeywordConfirmed(recentSearch.cityName)
            dismiss()
        }
        onDeleted = { recentSearch ->
            vm.onDeleteRecentSearch(recentSearch)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.TransparentBottomSheetTheme);
    }

    //We need to use activityViewModels() here because the ViewModel is shared across Fragments.
    private val vm: SearchViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_recent_searches_dialog, container, false)
        rv = view.findViewById(R.id.rvRecentSearches)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv.layoutManager = LinearLayoutManager(requireContext(), VERTICAL, false)
        rv.adapter = adapter
        rv.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        vm.recentSearches.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        rv.adapter = null
    }

    override fun onStart() {
        super.onStart()
        // A fix for rotated BottomSheetDialogFragment.
        // Reference:
        // https://stackoverflow.com/a/59156175/7870874
        val behavior = BottomSheetBehavior.from(requireView().parent as View)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    companion object {
        val TAG = this::class.simpleName!!
    }

}