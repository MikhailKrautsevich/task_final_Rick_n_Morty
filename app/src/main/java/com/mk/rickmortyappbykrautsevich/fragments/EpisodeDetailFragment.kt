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
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.mk.rickmortyappbykrautsevich.FragmentHost
import com.mk.rickmortyappbykrautsevich.HasBottomNavs
import com.mk.rickmortyappbykrautsevich.R
import com.mk.rickmortyappbykrautsevich.fragments.recyclers_data.EpisodeData
import com.mk.rickmortyappbykrautsevich.viewmodels.EpisodeDetailViewModel

class EpisodeDetailFragment : Fragment() {

    companion object {
        private const val ARGS_ID = "ARGS_ID"

        @JvmStatic
        fun newInstance(id: Int): EpisodeDetailFragment {
            val fragment = EpisodeDetailFragment()
            val bundle = Bundle()
            bundle.putInt(ARGS_ID, id)
            fragment.arguments = bundle
            return fragment
        }
    }

    private var hasBottomNavs: HasBottomNavs? = null
    private var fragmentHost: FragmentHost? = null

    private var recyclerView: RecyclerView? = null
    private var mainProgressBar: ProgressBar? = null

    private var nameTV: TextView? = null
    private var codeTV: TextView? = null
    private var airDateTV: TextView? = null
    private var idTV: TextView? = null
    private var urlTV: TextView? = null
    private var createdTV: TextView? = null

    private val viewModel: EpisodeDetailViewModel by lazy {
        ViewModelProvider(requireActivity()).get(EpisodeDetailViewModel::class.java)
    }
    private var epId = 0

    private var episodeLiveData : LiveData<EpisodeData>? = null
    private var loadingLiveData : LiveData<Boolean>? = null

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
        val v = inflater.inflate(R.layout.fragment_episode_detail, container, false)

        recyclerView = v.findViewById(R.id.recycler)
        mainProgressBar = v.findViewById(R.id.progress_bar)

        nameTV = v.findViewById(R.id.value_name_textview)
        codeTV = v.findViewById(R.id.value_code_textview)
        airDateTV = v.findViewById(R.id.value_air_date_textview)
        idTV = v.findViewById(R.id.value_id_textview)
        urlTV = v.findViewById(R.id.value_url_textview)
        createdTV = v.findViewById(R.id.value_created_textview)

        initPullToRefresh(v)
        return v
    }

    override fun onStart() {
        super.onStart()
        hasBottomNavs?.setButtonsEnabled(this)
        arguments?.let {
            epId = getEpId(it)
        }
        viewModel.loadData(epId)

        loadingLiveData = viewModel.getLoadingLiveData()
        loadingLiveData?.observe(viewLifecycleOwner) { t ->
            if (t == true) {
                mainProgressBar?.visibility = View.VISIBLE
            } else mainProgressBar?.visibility = View.INVISIBLE
        }

        episodeLiveData = viewModel.getEpisodeLiveData()
        episodeLiveData?.observe(viewLifecycleOwner
        ) { t ->
            t?.let {
                setData(it)
            }
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
            viewModel.loadData(epId)
            swipe.isRefreshing = false
        }
    }

    private fun setData(data: EpisodeData?) {
        data?.let {
            nameTV?.text = it.name
            codeTV?.text = it.episode
            airDateTV?.text = it.airDate
            idTV?.text = it.id.toString()
            urlTV?.text = it.url
            createdTV?.text = it.created
        }
    }

    private fun getEpId(bundle: Bundle): Int {
        return bundle.getInt(ARGS_ID)
    }
}