package com.mk.rickmortyappbykrautsevich.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.mk.rickmortyappbykrautsevich.presentation.activity.FragmentHost
import com.mk.rickmortyappbykrautsevich.presentation.activity.HasBottomNavs
import com.mk.rickmortyappbykrautsevich.R
import com.mk.rickmortyappbykrautsevich.data.app.App
import com.mk.rickmortyappbykrautsevich.presentation.fragments.recyclers.EpisodeAdapter
import com.mk.rickmortyappbykrautsevich.presentation.fragments.recyclers_data.CharacterData
import com.mk.rickmortyappbykrautsevich.presentation.fragments.recyclers_data.EpisodeData
import com.mk.rickmortyappbykrautsevich.presentation.viewmodels.CharacterDetailViewModel
import com.mk.rickmortyappbykrautsevich.presentation.viewmodels.interfaces.CharacterDetailViewModelInterface
import com.squareup.picasso.Picasso
import javax.inject.Inject

class CharacterDetailFragment : Fragment() {
    companion object {
        private const val ARGS_ID = "ARGS_ID"

        @JvmStatic
        fun newInstance(id: Int): CharacterDetailFragment {
            val fragment = CharacterDetailFragment()
            val bundle = Bundle()
            bundle.putInt(ARGS_ID, id)
            fragment.arguments = bundle
            return fragment
        }
    }

    @Inject
    lateinit var picasso : Picasso

    private var hasBottomNavs: HasBottomNavs? = null
    private var fragmentHost: FragmentHost? = null

    private var recyclerView: RecyclerView? = null
    private var mainProgressBar: ProgressBar? = null
    private var listProgressBar: ProgressBar? = null

    private var imageView: ImageView? = null
    private var nameTV: TextView? = null
    private var speciesTV: TextView? = null
    private var genderTV: TextView? = null
    private var statusTV: TextView? = null
    private var typeTV: TextView? = null
    private var originTV: TextView? = null
    private var locationTV: TextView? = null

    private val viewModel: CharacterDetailViewModelInterface by lazy {
        ViewModelProvider(this).get(CharacterDetailViewModel::class.java)
    }
    private var charId = -1

    private var charLiveData: LiveData<CharacterData>? = null
    private var epLoadingLiveData: LiveData<Boolean>? = null
    private var listLoadingLiveData: LiveData<Boolean>? = null
    private var listLiveData: LiveData<List<EpisodeData>>? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is HasBottomNavs)
            hasBottomNavs = context
        if (context is FragmentHost)
            fragmentHost = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_character_detail, container, false)
        recyclerView = v.findViewById(R.id.recycler)
        mainProgressBar = v.findViewById(R.id.progress_bar)
        listProgressBar = v.findViewById(R.id.list_progress_bar)

        imageView = v.findViewById(R.id.image)
        nameTV = v.findViewById(R.id.value_name_textview)
        speciesTV = v.findViewById(R.id.value_species_textview)
        genderTV = v.findViewById(R.id.value_gender_textview)
        statusTV = v.findViewById(R.id.value_status_textview)
        typeTV = v.findViewById(R.id.value_type_textview)
        originTV = v.findViewById(R.id.value_origin_textview)
        locationTV = v.findViewById(R.id.value_location_textview)

        initPullToRefresh(v)
        App.instance!!.component.inject(this)
        return v
    }

    override fun onStart() {
        super.onStart()
        hasBottomNavs?.setButtonsEnabled(this)

        recyclerView?.let {
            it.layoutManager = LinearLayoutManager(requireActivity())
            it.overScrollMode = View.OVER_SCROLL_NEVER
            val list: ArrayList<EpisodeData> = ArrayList()
            it.adapter = EpisodeAdapter(list, fragmentHost)
            it.visibility = View.INVISIBLE
        }

        arguments?.let {
            charId = getCharacterId(it)
        }
        viewModel.loadData(charId)

        epLoadingLiveData = viewModel.getCharLoadingLiveData()
        epLoadingLiveData?.observe(viewLifecycleOwner) { t ->
            if (t == true) {
                mainProgressBar?.visibility = View.VISIBLE
            } else mainProgressBar?.visibility = View.INVISIBLE
        }

        charLiveData = viewModel.getCharacterLiveData()
        charLiveData?.observe(
            viewLifecycleOwner
        ) { t ->
            t?.let {
                setData(it)
                viewModel.loadList(it.episode)
            }
        }

        listLoadingLiveData = viewModel.getListLoadingLiveData()
        listLoadingLiveData?.observe(viewLifecycleOwner) { t ->
            if (t == true) {
                listProgressBar?.visibility = View.VISIBLE
            } else listProgressBar?.visibility = View.INVISIBLE
        }

        listLiveData = viewModel.getListLiveData()
        listLiveData?.observe(viewLifecycleOwner) { t ->
            (recyclerView?.adapter as EpisodeAdapter).changeContacts(t)
            recyclerView?.visibility = View.VISIBLE
        }
    }

    override fun onDetach() {
        super.onDetach()
        hasBottomNavs = null
        fragmentHost = null
    }

    private fun initPullToRefresh(v: View) {
        val swipe: SwipeRefreshLayout = v.findViewById(R.id.swipe_layout)
        swipe.setOnRefreshListener {
            viewModel.loadData(charId)
            swipe.isRefreshing = false
        }
    }

    private fun setData(data: CharacterData?) {
        data?.let {
            nameTV?.text = it.name
            speciesTV?.text = it.species
            genderTV?.text = it.gender
            statusTV?.text = it.status
            typeTV?.text = it.type
            originTV?.text = it.origin?.name
            locationTV?.text = it.location?.name
            picasso.load(data.image).into(imageView)
        }
    }

    private fun getCharacterId(bundle: Bundle): Int {
        return bundle.getInt(ARGS_ID)
    }
}