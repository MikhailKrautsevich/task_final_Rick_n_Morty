package com.mk.rickmortyappbykrautsevich.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mk.rickmortyappbykrautsevich.R
import com.mk.rickmortyappbykrautsevich.fragments.recyclers_data.CharacterRecData
import com.squareup.picasso.Picasso

class CharactersListFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = CharactersListFragment()
    }

    private val picasso = Picasso.get()
    private var recyclerView: RecyclerView? = null
    private var progressBar: ProgressBar? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_characters_list, container, false)
        recyclerView = v.findViewById(R.id.recyclerCharacters)
        progressBar = v.findViewById(R.id.progress_bar)
        return v
    }

    override fun onStart() {
        super.onStart()
        recyclerView?.apply {
            layoutManager = LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false)
            adapter = CharacterAdapter(ArrayList())
        }
    }

    inner class CharacterAdapter(private var characters: List<CharacterRecData>) :
        RecyclerView.Adapter<CharacterAdapter.CharacterHolder>() {

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): CharacterAdapter.CharacterHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.character_holder, parent, false)
            return CharacterHolder(itemView)
        }

        override fun onBindViewHolder(holder: CharacterAdapter.CharacterHolder, position: Int) {
            holder.bind(characters[position])
        }

        override fun getItemCount(): Int = characters.size

        fun changeContacts(list: List<CharacterRecData>) {
            val oldCharacters = characters
            val diffUtilCallback = ContactDiffUtilCallBack(oldList = oldCharacters, newList = list)
            val result = DiffUtil.calculateDiff(diffUtilCallback, false)
            characters = list
            result.dispatchUpdatesTo(this)
        }

        inner class CharacterHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
            View.OnClickListener {
            private val image: ImageView = itemView.findViewById(R.id.char_image)
            private val nameTextView: TextView = itemView.findViewById(R.id.name_textview)
            private val specTextView: TextView = itemView.findViewById(R.id.spec_textview)
            private val genderTextView: TextView = itemView.findViewById(R.id.gender_textview)
            private val statusTextView: TextView = itemView.findViewById(R.id.status_textview)
            private var characterBound: CharacterRecData? = null

            init {
                itemView.setOnClickListener { this }
            }

            fun bind(character: CharacterRecData) {
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
            private val oldList: List<CharacterRecData>,
            private val newList: List<CharacterRecData>
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