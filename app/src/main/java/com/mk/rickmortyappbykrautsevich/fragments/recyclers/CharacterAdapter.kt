package com.mk.rickmortyappbykrautsevich.fragments.recyclers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mk.rickmortyappbykrautsevich.FragmentHost
import com.mk.rickmortyappbykrautsevich.R
import com.mk.rickmortyappbykrautsevich.fragments.recyclers_data.CharacterData
import com.squareup.picasso.Picasso

class CharacterAdapter(
    private var characters: List<CharacterData>,
    private val picasso: Picasso,
    private val host: FragmentHost?
) :
    RecyclerView.Adapter<CharacterAdapter.CharacterHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CharacterAdapter.CharacterHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.holder_character, parent, false)
        return CharacterHolder(itemView)
    }

    override fun onBindViewHolder(holder: CharacterAdapter.CharacterHolder, position: Int) {
        holder.bind(characters[position])
    }

    override fun getItemCount(): Int = characters.size

    fun changeContacts(list: List<CharacterData>) {
        val new = ArrayList<CharacterData>()
        new.addAll(list)
        val old = characters
        val diffUtilCallback = ContactDiffUtilCallBack(oldList = old, newList = new)
        val result = DiffUtil.calculateDiff(diffUtilCallback, false)
        characters = new
        result.dispatchUpdatesTo(this)
    }

    inner class CharacterHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        private val image: ImageView = itemView.findViewById(R.id.char_image)
        private val nameTextView: TextView = itemView.findViewById(R.id.name_textview)
        private val specTextView: TextView = itemView.findViewById(R.id.spec_textview)
        private val genderTextView: TextView = itemView.findViewById(R.id.gender_textview)
        private val statusTextView: TextView = itemView.findViewById(R.id.status_textview)
        private var characterBound: CharacterData? = null

        init {
            itemView.setOnClickListener { this }
        }

        fun bind(character: CharacterData) {
            characterBound = character
            nameTextView.text = character.name
            specTextView.text = character.species
            genderTextView.text = character.gender
            statusTextView.text = character.status
            picasso.load(character.image).into(image)
        }

        override fun onClick(p0: View?) {
            TODO("Not yet implemented")
        }

    }

    inner class ContactDiffUtilCallBack(
        private val oldList: List<CharacterData>,
        private val newList: List<CharacterData>
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