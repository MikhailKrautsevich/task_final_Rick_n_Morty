package com.mk.rickmortyappbykrautsevich.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.mk.rickmortyappbykrautsevich.FragmentHost
import com.mk.rickmortyappbykrautsevich.HasBottomNavs
import com.mk.rickmortyappbykrautsevich.R
import com.mk.rickmortyappbykrautsevich.fragments.recyclers.LocationAdapter
import com.mk.rickmortyappbykrautsevich.fragments.recyclers_data.LocationData
import com.mk.rickmortyappbykrautsevich.retrofit.models.queries.LocationQuery
import com.mk.rickmortyappbykrautsevich.viewmodels.AllLocationViewModel

class LocationListFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = LocationListFragment()
    }

    private var hasBottomNavs: HasBottomNavs? = null
    private var host: FragmentHost? = null
    private var recyclerView: RecyclerView? = null
    private var noResults: TextView? = null
    private var showFilters: Button? = null
    private var useFilters: Button? = null
    private var filterContainer: ViewGroup? = null
    private var filterName: EditText? = null
    private var filterType: EditText? = null
    private var filterDimension: EditText? = null
    private var mainProgressBar: ProgressBar? = null
    private var pagingProgressBar: ProgressBar? = null
    private val viewModel: AllLocationViewModel by lazy {
        ViewModelProvider(requireActivity()).get(AllLocationViewModel::class.java)
    }

    private var loadingLiveData: LiveData<Boolean>? = null
    private var pagingLiveData: LiveData<Boolean>? = null
    private var locationsLiveData: LiveData<List<LocationData>>? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is HasBottomNavs)
            hasBottomNavs = context
        if (context is FragmentHost)
            host = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_location_list, container, false)
        recyclerView = v.findViewById(R.id.recyclerLocations)
        noResults = v.findViewById(R.id.no_results_textview)
        showFilters = v.findViewById(R.id.filters_button)
        filterContainer = v.findViewById(R.id.filters_container)
        filterName = v.findViewById(R.id.edit_name)
        filterType = v.findViewById(R.id.edit_type)
        filterDimension = v.findViewById(R.id.edit_dimension)
        useFilters = v.findViewById(R.id.use_filter)
        mainProgressBar = v.findViewById(R.id.progress_bar)
        pagingProgressBar = v.findViewById(R.id.paging_progress_bar)

        initPullToRefresh(v)
        return v
    }

    override fun onStart() {
        super.onStart()
        hasBottomNavs?.setButtonsEnabled(this)
        loadingLiveData = viewModel.getLoadingLiveData()
        loadingLiveData?.observe(
            viewLifecycleOwner
        ) { loading ->
            loading?.let {
                if (it) {
                    mainProgressBar?.visibility = View.VISIBLE
                } else mainProgressBar?.visibility = View.INVISIBLE
            }
        }

        pagingLiveData = viewModel.getPaginationLiveData()
        pagingLiveData?.observe(
            viewLifecycleOwner
        ) { loading ->
            loading?.let {
                if (it) {
                    pagingProgressBar?.visibility = View.VISIBLE
                } else pagingProgressBar?.visibility = View.GONE
            }
        }

        locationsLiveData = viewModel.getLocationsList()
        recyclerView?.apply {
            layoutManager = LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false)
            overScrollMode = View.OVER_SCROLL_NEVER
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val totalItemCount = layoutManager.itemCount
                    val lastVisible = layoutManager.findLastVisibleItemPosition()

                    val endHasBeenReached = (lastVisible + 3) >= totalItemCount
                    if (endHasBeenReached) {
                        viewModel.getMoreData()
                    }
                }
            })

            adapter = LocationAdapter(ArrayList(), host)
        }
        locationsLiveData?.observe(viewLifecycleOwner) { list ->
            list?.let {
                (recyclerView?.adapter as LocationAdapter).changeData(it)
                if (it.isNotEmpty()) {
                    showRecycler()
                } else showNoResults()
            }
        }
        initShowButtonListener()
        initUseFilterButtonListener()
    }

    override fun onDetach() {
        super.onDetach()
        hasBottomNavs = null
        host = null
    }

    private fun initShowButtonListener() {
        showFilters?.setOnClickListener { v ->
            val button = v as Button
            val show = getString(R.string.show_filters_button)
            val hide = getString(R.string.hide_filters_button)
            if (button.text == show) {
                button.text = hide
                filterContainer?.visibility = View.VISIBLE
            } else {
                button.text = show
                filterContainer?.visibility = View.GONE
            }
        }
    }

    private fun initUseFilterButtonListener() {
        useFilters?.setOnClickListener {
            val name = filterName?.text?.toString()
            val type = filterType?.text?.toString()
            val dimen = filterDimension?.text?.toString()
            var query: LocationQuery? = null
            if (name?.isBlank() == false
                || type?.isBlank() == false
                || dimen?.isBlank() == false
            ) {
                query = LocationQuery(
                    name = name,
                    type = type,
                    dimension = dimen
                )
            }
            viewModel.getData(query)
        }
    }

    private fun showRecycler() {
        recyclerView?.visibility = View.VISIBLE
        noResults?.visibility = View.INVISIBLE
    }

    private fun showNoResults() {
        recyclerView?.visibility = View.INVISIBLE
        noResults?.visibility = View.VISIBLE
    }

    private fun initPullToRefresh(v: View) {
        val swipe: SwipeRefreshLayout = v.findViewById(R.id.swipe_layout)
        swipe.setOnRefreshListener {
            viewModel.getData(null)
            swipe.isRefreshing = false
        }
    }
}