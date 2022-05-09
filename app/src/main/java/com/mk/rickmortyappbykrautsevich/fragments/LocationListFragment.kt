package com.mk.rickmortyappbykrautsevich.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.mk.rickmortyappbykrautsevich.viewmodels.AllLocationViewModel

class LocationListFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = LocationListFragment()
    }

    private var hasBottomNavs: HasBottomNavs? = null
    private var recyclerView: RecyclerView? = null
    private var mainProgressBar: ProgressBar? = null
    private var pagingProgressBar: ProgressBar? = null
    private val viewModel: AllLocationViewModel by lazy {
        ViewModelProvider(requireActivity()).get(AllLocationViewModel::class.java)
    }

    private var loadingLiveData: LiveData<Boolean>? = null
    private var pagingLiveData: LiveData<Boolean>? = null
    private var hasNextPageLiveData: LiveData<Boolean>? = null
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
            }
        }

        hasNextPageLiveData = viewModel.getHasNextPageLiveData()
        hasNextPageLiveData?.observe(
            viewLifecycleOwner
        ) { t ->
            if (t == false) {
                recyclerView?.clearOnScrollListeners()
                pagingProgressBar?.visibility = View.GONE
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        hasBottomNavs = null
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