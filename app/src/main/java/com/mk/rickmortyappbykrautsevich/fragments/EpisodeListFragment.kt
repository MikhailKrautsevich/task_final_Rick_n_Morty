package com.mk.rickmortyappbykrautsevich.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.mk.rickmortyappbykrautsevich.FragmentHost
import com.mk.rickmortyappbykrautsevich.HasBottomNavs
import com.mk.rickmortyappbykrautsevich.R
import com.mk.rickmortyappbykrautsevich.fragments.recyclers_data.EpisodeData
import com.mk.rickmortyappbykrautsevich.retrofit.models.queries.EpisodeQuery
import com.mk.rickmortyappbykrautsevich.viewmodels.AllEpisodesViewModel

class EpisodeListFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = EpisodeListFragment()
    }

    private var hasBottomNavs: HasBottomNavs? = null
    private var fragmentHost: FragmentHost? = null

    private var recyclerView: RecyclerView? = null
    private var noResults: TextView? = null
    private var showFilters: Button? = null
    private var useFilters: Button? = null
    private var filterContainer: ViewGroup? = null
    private var filterName: EditText? = null
    private var filterCode: EditText? = null
    private var mainProgressBar: ProgressBar? = null
    private var pagingProgressBar: ProgressBar? = null
    private val viewModel: AllEpisodesViewModel by lazy {
        ViewModelProvider(requireActivity()).get(AllEpisodesViewModel::class.java)
    }

    private var loadingLiveData: LiveData<Boolean>? = null
    private var pagingLiveData: LiveData<Boolean>? = null
    private var episodeLiveData: LiveData<List<EpisodeData>>? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is HasBottomNavs)
            hasBottomNavs = context
        if (context is FragmentHost)
            fragmentHost = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_episode_list, container, false)
        recyclerView = v.findViewById(R.id.recyclerEpisodes)
        noResults = v.findViewById(R.id.no_results_textview)
        showFilters = v.findViewById(R.id.filters_button)
        filterContainer = v.findViewById(R.id.filters_container)
        filterName = v.findViewById(R.id.edit_name)
        filterCode = v.findViewById(R.id.edit_code)
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
                    recyclerView?.visibility = View.INVISIBLE
                } else {
                    mainProgressBar?.visibility = View.INVISIBLE
                    recyclerView?.visibility = View.VISIBLE
                }
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

        episodeLiveData = viewModel.getEpisodesList()
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
                        Log.d("12347", "fragment: endHasBeenReached")
                    }
                }
            })

            adapter = EpisodeAdapter(ArrayList())
        }
        episodeLiveData?.observe(viewLifecycleOwner) { list ->
            list?.let {
                (recyclerView?.adapter as EpisodeListFragment.EpisodeAdapter).changeContacts(it)
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
            val code = filterCode?.text?.toString()
            var query: EpisodeQuery? = null
            if (name?.isBlank() == false || code?.isBlank() == false) {
                query = EpisodeQuery(
                    name = name,
                    code = code
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

    inner class EpisodeAdapter(private var episodes: List<EpisodeData>) :
        RecyclerView.Adapter<EpisodeAdapter.EpisodeHolder>() {

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): EpisodeAdapter.EpisodeHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.holder_episode, parent, false)
            return EpisodeHolder(itemView)
        }

        override fun onBindViewHolder(holder: EpisodeAdapter.EpisodeHolder, position: Int) {
            holder.bind(episodes[position])
        }

        override fun getItemCount(): Int = episodes.size

        fun changeContacts(list: List<EpisodeData>) {
            val new = ArrayList<EpisodeData>()
            new.addAll(list)
            val old = episodes
            val diffUtilCallback = ContactDiffUtilCallBack(oldList = old, newList = new)
            val result = DiffUtil.calculateDiff(diffUtilCallback, false)
            episodes = new
            result.dispatchUpdatesTo(this)
        }

        inner class EpisodeHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val nameTextView: TextView = itemView.findViewById(R.id.ep_name_textview)
            private val numberTextView: TextView = itemView.findViewById(R.id.ep_number_textview)
            private val dateTextView: TextView = itemView.findViewById(R.id.ep_air_date_textview)
            private var episodeBound: EpisodeData? = null

            init {
                itemView.setOnClickListener {
                    val fragment =
                        episodeBound?.id?.let { EpisodeDetailFragment.newInstance(it) }
                    fragment?.let {
                        fragmentHost?.setFragment(it)
                    }
                }
            }

            fun bind(episode: EpisodeData) {
                episodeBound = episode
                nameTextView.text = episode.name
                numberTextView.text = episode.episode
                dateTextView.text = episode.airDate
            }
        }

        inner class ContactDiffUtilCallBack(
            private val oldList: List<EpisodeData>,
            private val newList: List<EpisodeData>
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