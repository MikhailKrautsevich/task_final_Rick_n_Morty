package com.mk.rickmortyappbykrautsevich.presentation.fragments

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.mk.rickmortyappbykrautsevich.presentation.activity.FragmentHost
import com.mk.rickmortyappbykrautsevich.presentation.activity.HasBottomNavs
import com.mk.rickmortyappbykrautsevich.R
import com.mk.rickmortyappbykrautsevich.data.app.App
import com.mk.rickmortyappbykrautsevich.presentation.fragments.recyclers.CharacterAdapter
import com.mk.rickmortyappbykrautsevich.presentation.fragments.recyclers_data.CharacterData
import com.mk.rickmortyappbykrautsevich.presentation.fragments.recyclers_data.LocationData
import com.mk.rickmortyappbykrautsevich.presentation.viewmodels.LocationDetailViewModel
import com.mk.rickmortyappbykrautsevich.presentation.viewmodels.interfaces.LocationDetailViewModelInterface
import com.squareup.picasso.Picasso
import javax.inject.Inject

class LocationDetailFragment : Fragment() {

    companion object {
        private const val ARGS_ID = "ARGS_ID"

        @JvmStatic
        fun newInstance(id: Int): LocationDetailFragment {
            val fragment = LocationDetailFragment()
            val bundle = Bundle()
            bundle.putInt(ARGS_ID, id)
            fragment.arguments = bundle
            return fragment
        }
    }

    @Inject
    lateinit var picasso : Picasso

    private var hasBottomNavs: HasBottomNavs? = null
    private var host: FragmentHost? = null

    private var recyclerView: RecyclerView? = null
    private var mainProgressBar: ProgressBar? = null
    private var listProgressBar: ProgressBar? = null

    private var nameTV: TextView? = null
    private var typeTV: TextView? = null
    private var dimensionTV: TextView? = null

    private val viewModel: LocationDetailViewModelInterface by lazy {
        ViewModelProvider(this).get(LocationDetailViewModel::class.java)
    }
    private var locId = -1

    private var locLiveData: LiveData<LocationData>? = null
    private var epLoadingLiveData: LiveData<Boolean>? = null
    private var listLoadingLiveData: LiveData<Boolean>? = null
    private var listLiveData: LiveData<List<CharacterData>>? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is HasBottomNavs)
            hasBottomNavs = context
        if (context is FragmentHost)
            host = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_location_detail, container, false)

        recyclerView = v.findViewById(R.id.recycler)
        mainProgressBar = v.findViewById(R.id.progress_bar)
        listProgressBar = v.findViewById(R.id.list_progress_bar)

        nameTV = v.findViewById(R.id.value_name_textview)
        typeTV = v.findViewById(R.id.value_type_textview)
        dimensionTV = v.findViewById(R.id.value_dimen_textview)

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
            val list: ArrayList<CharacterData> = ArrayList()
            it.adapter = CharacterAdapter(list, picasso, host)
            it.visibility = View.INVISIBLE
        }

        arguments?.let {
            locId = getEpId(it)
        }
        viewModel.loadData(locId)

        epLoadingLiveData = viewModel.getLocLoadingLiveData()
        epLoadingLiveData?.observe(viewLifecycleOwner) { t ->
            if (t == true) {
                mainProgressBar?.visibility = View.VISIBLE
            } else mainProgressBar?.visibility = View.INVISIBLE
        }

        locLiveData = viewModel.getLocationLiveData()
        locLiveData?.observe(
            viewLifecycleOwner
        ) { t ->
            t?.let {
                setData(it)
                viewModel.loadList(it.residents)
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
            (recyclerView?.adapter as CharacterAdapter).changeContacts(t)
            recyclerView?.visibility = View.VISIBLE
        }
    }

    override fun onDetach() {
        super.onDetach()
        hasBottomNavs = null
        host = null
    }

    private fun initPullToRefresh(v: View) {
        val swipe: SwipeRefreshLayout = v.findViewById(R.id.swipe_layout)
        swipe.setOnRefreshListener {
            viewModel.loadData(locId)
            swipe.isRefreshing = false
        }
    }

    private fun setData(data: LocationData?) {
        data?.let {
            nameTV?.text = it.name
            typeTV?.text = it.type
            dimensionTV?.text = it.dimension
        }
    }

    private fun getEpId(bundle: Bundle): Int {
        return bundle.getInt(ARGS_ID)
    }
}