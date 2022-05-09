package com.mk.rickmortyappbykrautsevich.fragments

import android.content.Context
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
import com.mk.rickmortyappbykrautsevich.HasBottomNavs
import com.mk.rickmortyappbykrautsevich.R
import com.mk.rickmortyappbykrautsevich.fragments.recyclers_data.EpisodeRecData
import com.mk.rickmortyappbykrautsevich.viewmodels.AllEpisodesViewModel

class EpisodeListFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = EpisodeListFragment()
    }

    private var hasBottomNavs: HasBottomNavs? = null
    private var recyclerView: RecyclerView? = null
    private var mainProgressBar: ProgressBar? = null
    private var pagingProgressBar: ProgressBar? = null
    private val viewModel: AllEpisodesViewModel by lazy {
        ViewModelProvider(requireActivity()).get(AllEpisodesViewModel::class.java)
    }

    private var loadingLiveData: LiveData<Boolean>? = null
    private var pagingLiveData: LiveData<Boolean>? = null
    private var episodeLiveData: LiveData<List<EpisodeRecData>>? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is HasBottomNavs)
            hasBottomNavs = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_episode_list, container, false)
        recyclerView = v.findViewById(R.id.recyclerEpisodes)
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
                    }
                }
            })

            adapter = EpisodeAdapter(ArrayList())
        }
        episodeLiveData?.observe(viewLifecycleOwner) { list ->
            list?.let {
                (recyclerView?.adapter as EpisodeListFragment.EpisodeAdapter).changeContacts(it)
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        hasBottomNavs = null
    }

    inner class EpisodeAdapter(private var episodes: List<EpisodeRecData>) :
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

        fun changeContacts(list: List<EpisodeRecData>) {
            val new = ArrayList<EpisodeRecData>()
            new.addAll(list)
            val old = episodes
            val diffUtilCallback = ContactDiffUtilCallBack(oldList = old, newList = new)
            val result = DiffUtil.calculateDiff(diffUtilCallback, false)
            episodes = new
            result.dispatchUpdatesTo(this)
        }

        inner class EpisodeHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
            View.OnClickListener {
            private val nameTextView: TextView = itemView.findViewById(R.id.ep_name_textview)
            private val numberTextView: TextView = itemView.findViewById(R.id.ep_number_textview)
            private val dateTextView: TextView = itemView.findViewById(R.id.ep_air_date_textview)
            private var episodeBound: EpisodeRecData? = null

            init {
                itemView.setOnClickListener { this }
            }

            fun bind(episode: EpisodeRecData) {
                episodeBound = episode
                nameTextView.text = episode.name
                numberTextView.text = episode.episode
                dateTextView.text = episode.airDate
            }

            override fun onClick(p0: View?) {
                TODO("Not yet implemented")
            }
        }

        inner class ContactDiffUtilCallBack(
            private val oldList: List<EpisodeRecData>,
            private val newList: List<EpisodeRecData>
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