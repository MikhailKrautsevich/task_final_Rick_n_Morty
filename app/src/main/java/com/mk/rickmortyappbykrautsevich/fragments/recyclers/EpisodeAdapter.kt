package com.mk.rickmortyappbykrautsevich.fragments.recyclers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mk.rickmortyappbykrautsevich.FragmentHost
import com.mk.rickmortyappbykrautsevich.R
import com.mk.rickmortyappbykrautsevich.fragments.EpisodeDetailFragment
import com.mk.rickmortyappbykrautsevich.fragments.recyclers_data.EpisodeData

class EpisodeAdapter(private var episodes: List<EpisodeData>, private val host: FragmentHost?) :
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
                    host?.setFragment(it)
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