package com.mk.rickmortyappbykrautsevich.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mk.rickmortyappbykrautsevich.HasBottomNavs
import com.mk.rickmortyappbykrautsevich.R
import com.mk.rickmortyappbykrautsevich.enums.Gender
import com.mk.rickmortyappbykrautsevich.enums.Status
import com.mk.rickmortyappbykrautsevich.fragments.recyclers_data.CharacterRecData
import com.mk.rickmortyappbykrautsevich.retrofit.models.queries.CharacterQuery
import com.mk.rickmortyappbykrautsevich.viewmodels.AllCharactersViewModel
import com.squareup.picasso.Picasso

class CharactersListFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = CharactersListFragment()
    }

    private var hasBottomNavs: HasBottomNavs? = null
    private val picasso = Picasso.get()
    private var recyclerView: RecyclerView? = null
    private var showFilters: Button? = null
    private var useFilters: Button? = null
    private var filterContainer: ViewGroup? = null
    private var filterName: EditText? = null
    private var filterSpecies: EditText? = null
    private var filterType: EditText? = null
    private var filterStatus: Spinner? = null
    private var filterGender: Spinner? = null
    private var filterStatusPos: Int = -1
    private var filterGenderPos: Int = -1
    private var mainProgressBar: ProgressBar? = null
    private var pagingProgressBar: ProgressBar? = null
    private val viewModel: AllCharactersViewModel by lazy {
        ViewModelProvider(requireActivity()).get(AllCharactersViewModel::class.java)
    }

    private var loadingLiveData: LiveData<Boolean>? = null
    private var pagingLiveData: LiveData<Boolean>? = null
    private var charactersLiveData: LiveData<List<CharacterRecData>>? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is HasBottomNavs)
            hasBottomNavs = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_characters_list, container, false)
        recyclerView = v.findViewById(R.id.recyclerCharacters)
        showFilters = v.findViewById(R.id.filters_button)
        filterContainer = v.findViewById(R.id.filters_container)
        filterName = v.findViewById(R.id.edit_name)
        filterType = v.findViewById(R.id.edit_type)
        filterSpecies = v.findViewById(R.id.edit_species)
        filterStatus = v.findViewById(R.id.spinner_status)
        filterGender = v.findViewById(R.id.spinner_gender)
        useFilters = v.findViewById(R.id.use_filter)
        mainProgressBar = v.findViewById(R.id.progress_bar)
        pagingProgressBar = v.findViewById(R.id.paging_progress_bar)
        return v
    }

    override fun onStart() {
        super.onStart()
        hasBottomNavs?.setButtonsEnabled(this)
        initSpinners()
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
        charactersLiveData = viewModel.getCharactersList()

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

            adapter = CharacterAdapter(ArrayList())
        }
        charactersLiveData?.observe(
            viewLifecycleOwner
        ) { list ->
            list?.let {
                (recyclerView?.adapter as CharacterAdapter).changeContacts(it)
            }
        }
        initShowButtonListener()
        initUseFilterButtonListener()
    }

    override fun onDetach() {
        super.onDetach()
        hasBottomNavs = null
    }

    private fun initSpinners() {
        val res = requireActivity().resources

        val statuses = res.getStringArray(R.array.status)
        val statAdapter = ArrayAdapter(
            requireActivity().baseContext,
            R.layout.spinner_item,
            R.id.textValue,
            statuses
        )
        filterStatus?.adapter = statAdapter

        val genders = res.getStringArray(R.array.gender)
        val gendersAdapter = ArrayAdapter(
            requireActivity().baseContext,
            R.layout.spinner_item,
            R.id.textValue,
            genders
        )
        filterGender?.adapter = gendersAdapter

        filterStatus?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                filterStatusPos = p2
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        filterGender?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                filterGenderPos = p2
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
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
            val species = filterSpecies?.text?.toString()
            var gender: Gender? = null
            if (filterGenderPos in 1..4) {
                gender = Gender.fromInt(filterGenderPos)
            }
            var status: Status? = null
            if (filterStatusPos in 1..3) {
                status = Status.fromInt(filterStatusPos)
            }
            var query: CharacterQuery? = null
            if (name?.isBlank() == false
                || species?.isBlank() == false
                || type?.isBlank() == false
                || gender == null
                || status == null
            ) {
                query = CharacterQuery(
                    name = name,
                    type = type,
                    species = species,
                    gender = gender?.name,
                    status = status?.name
                )
                Log.d("12345", query.gender.toString())
                Log.d("12345", query.status.toString())
            }
            viewModel.getData(query)
        }
    }

    inner class CharacterAdapter(private var characters: List<CharacterRecData>) :
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

        fun changeContacts(list: List<CharacterRecData>) {
            val new = ArrayList<CharacterRecData>()
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