package com.mk.rickmortyappbykrautsevich.fragments

import android.os.Bundle
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
import com.mk.rickmortyappbykrautsevich.R
import com.mk.rickmortyappbykrautsevich.fragments.recyclers_data.LocationRecData
import com.mk.rickmortyappbykrautsevich.viewmodels.AllLocationViewModel

class LocationListFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = LocationListFragment()
    }

    private var recyclerView: RecyclerView? = null
    private var progressBar: ProgressBar? = null
    private val viewModel: AllLocationViewModel by lazy {
        ViewModelProvider(requireActivity()).get(AllLocationViewModel::class.java)
    }

    private var loadingLiveData: LiveData<Boolean>? = null
    private var locationsLiveData: LiveData<List<LocationRecData>>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_location_list, container, false)
        recyclerView = v.findViewById(R.id.recyclerCharacters)
        progressBar = v.findViewById(R.id.progress_bar)
        return v
    }

    override fun onStart() {
        super.onStart()
        loadingLiveData = viewModel.getLoadingLiveData()
        loadingLiveData?.observe(
            viewLifecycleOwner
        ) { loading ->
            loading?.let {
                if (it) {
                    progressBar?.visibility = View.VISIBLE
                } else progressBar?.visibility = View.INVISIBLE
            }
        }
        locationsLiveData = viewModel.getLocationsList()

        recyclerView?.apply {
            layoutManager = LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false)
            adapter = LocationAdapter(ArrayList())
        }
        locationsLiveData?.observe(viewLifecycleOwner) { list ->
            list?.let {
                (recyclerView?.adapter as LocationAdapter).changeContacts(it)
            }
        }
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

        fun changeContacts(list: List<LocationRecData>) {
            val old = locs
            val diffUtilCallback = ContactDiffUtilCallBack(oldList = old, newList = list)
            val result = DiffUtil.calculateDiff(diffUtilCallback, false)
            locs = list
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