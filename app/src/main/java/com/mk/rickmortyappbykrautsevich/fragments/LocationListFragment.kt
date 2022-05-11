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
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mk.rickmortyappbykrautsevich.HasBottomNavs
import com.mk.rickmortyappbykrautsevich.R
import com.mk.rickmortyappbykrautsevich.fragments.recyclers_data.LocationRecData
import com.mk.rickmortyappbykrautsevich.retrofit.models.queries.LocationQuery
import com.mk.rickmortyappbykrautsevich.viewmodels.AllLocationViewModel

class LocationListFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = LocationListFragment()
    }

    private var hasBottomNavs: HasBottomNavs? = null
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
    private var locationsLiveData: LiveData<List<LocationRecData>>? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is HasBottomNavs)
            hasBottomNavs = context
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

            adapter = LocationAdapter(ArrayList())
        }
        locationsLiveData?.observe(viewLifecycleOwner) { list ->
            list?.let {
                (recyclerView?.adapter as LocationAdapter).changeData(it)
                if (it.isNotEmpty()) {showRecycler()} else showNoResults()
            }
        }
        initShowButtonListener()
        initUseFilterButtonListener()
    }

    override fun onDetach() {
        super.onDetach()
        hasBottomNavs = null
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

    private fun showRecycler(){
        recyclerView?.visibility = View.VISIBLE
        noResults?.visibility = View.INVISIBLE
    }

    private fun showNoResults(){
        recyclerView?.visibility = View.INVISIBLE
        noResults?.visibility = View.VISIBLE
    }

    inner class LocationAdapter(private var locs: List<LocationRecData>) :
        RecyclerView.Adapter<LocationAdapter.LocationHolder>() {

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): LocationAdapter.LocationHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.holder_location, parent, false)
            return LocationHolder(itemView)
        }

        override fun onBindViewHolder(holder: LocationAdapter.LocationHolder, position: Int) {
            holder.bind(locs[position])
        }

        override fun getItemCount(): Int = locs.size

        fun changeData(list: List<LocationRecData>) {
            val new = ArrayList<LocationRecData>()
            new.addAll(list)
            val old = locs
            val diffUtilCallback = ContactDiffUtilCallBack(oldList = old, newList = new)
            val result = DiffUtil.calculateDiff(diffUtilCallback, false)
            locs = new
            result.dispatchUpdatesTo(this)
        }

        inner class LocationHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
            View.OnClickListener {
            private val locNameTextView: TextView = itemView.findViewById(R.id.loc_name_textview)
            private val locTypeTextView: TextView = itemView.findViewById(R.id.loc_type_textview)
            private val locDimenTextView: TextView = itemView.findViewById(R.id.loc_dimen_textview)
            private var locBound: LocationRecData? = null

            init {
                itemView.setOnClickListener { this }
            }

            fun bind(loc: LocationRecData) {
                locBound = loc
                locNameTextView.text = loc.name
                locTypeTextView.text = loc.type
                locDimenTextView.text = loc.dimension
            }

            override fun onClick(p0: View?) {
                TODO("Not yet implemented")
            }

        }

        inner class ContactDiffUtilCallBack(
            private val oldList: List<LocationRecData>,
            private val newList: List<LocationRecData>
        ) : DiffUtil.Callback() {
            override fun getOldListSize() = oldList.size

            override fun getNewListSize() = newList.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                (oldList[oldItemPosition].id == newList[newItemPosition].id)

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldList[oldItemPosition] == newList[newItemPosition]
            }
        }
    }
}