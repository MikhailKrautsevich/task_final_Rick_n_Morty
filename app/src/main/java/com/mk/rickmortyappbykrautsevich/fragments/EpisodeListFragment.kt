package com.mk.rickmortyappbykrautsevich.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mk.rickmortyappbykrautsevich.R
import com.mk.rickmortyappbykrautsevich.fragments.recyclers_data.EpisodeRecData

class EpisodeListFragment: Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = EpisodeListFragment()
    }

    private var recyclerView: RecyclerView? = null
    private var progressBar: ProgressBar? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_episode_list, container, false)
        recyclerView = v.findViewById(R.id.recyclerCharacters)
        progressBar = v.findViewById(R.id.progress_bar)
        return v
    }

    override fun onStart() {
        super.onStart()
        recyclerView?.apply {
            layoutManager = LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false)
            adapter = EpisodeAdapter(ArrayList())
        }
    }

    inner class EpisodeAdapter(private var episodes: List<EpisodeRecData>) :
        RecyclerView.Adapter<EpisodeAdapter.EpisodeHolder>() {

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): EpisodeAdapter.EpisodeHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.episode_holder, parent, false)
            return EpisodeHolder(itemView)
        }

        override fun onBindViewHolder(holder: EpisodeAdapter.EpisodeHolder, position: Int) {
            holder.bind(episodes[position])
        }

        override fun getItemCount(): Int = episodes.size

        fun changeContacts(list: List<EpisodeRecData>) {
            val old = episodes
            val diffUtilCallback = ContactDiffUtilCallBack(oldList = old, newList = list)
            val result = DiffUtil.calculateDiff(diffUtilCallback, false)
            episodes = list
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
                numberTextView.text = episode.episodeNumber.toString()
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