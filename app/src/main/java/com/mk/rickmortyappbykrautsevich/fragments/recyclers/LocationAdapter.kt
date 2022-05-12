package com.mk.rickmortyappbykrautsevich.fragments.recyclers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mk.rickmortyappbykrautsevich.FragmentHost
import com.mk.rickmortyappbykrautsevich.R
import com.mk.rickmortyappbykrautsevich.fragments.recyclers_data.LocationRecData

class LocationAdapter(
    private var locs: List<LocationRecData>,
    private val host: FragmentHost?
) :
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