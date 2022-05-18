package com.mk.rickmortyappbykrautsevich.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.mk.rickmortyappbykrautsevich.presentation.activity.FragmentHost
import com.mk.rickmortyappbykrautsevich.presentation.activity.HasBottomNavs
import com.mk.rickmortyappbykrautsevich.R
import com.mk.rickmortyappbykrautsevich.data.app.App
import com.mk.rickmortyappbykrautsevich.presentation.fragments.enums.Gender
import com.mk.rickmortyappbykrautsevich.presentation.fragments.enums.Status
import com.mk.rickmortyappbykrautsevich.presentation.fragments.recyclers.CharacterAdapter
import com.mk.rickmortyappbykrautsevich.presentation.fragments.recyclers_data.CharacterData
import com.mk.rickmortyappbykrautsevich.presentation.fragments.utils.SearchViewUtil
import com.mk.rickmortyappbykrautsevich.data.retrofit.models.queries.CharacterQuery
import com.mk.rickmortyappbykrautsevich.presentation.viewmodels.AllCharactersViewModel
import com.mk.rickmortyappbykrautsevich.presentation.viewmodels.interfaces.AllCharactersViewModelInterface
import com.squareup.picasso.Picasso
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CharactersListFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = CharactersListFragment()
    }

    @Inject
    lateinit var picasso : Picasso

    private var hasBottomNavs: HasBottomNavs? = null
    private var host: FragmentHost? = null
    private var recyclerView: RecyclerView? = null
    private var noResults: TextView? = null
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
    private val viewModel: AllCharactersViewModelInterface by lazy {
        ViewModelProvider(requireActivity()).get(AllCharactersViewModel::class.java)
    }

    private var loadingLiveData: LiveData<Boolean>? = null
    private var pagingLiveData: LiveData<Boolean>? = null
    private var charactersLiveData: LiveData<List<CharacterData>>? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is HasBottomNavs)
            hasBottomNavs = context
        if (context is FragmentHost)
            host = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_characters_list, container, false)
        recyclerView = v.findViewById(R.id.recyclerCharacters)
        noResults = v.findViewById(R.id.no_results_textview)
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

        initPullToRefresh(v)
        App.instance!!.component.inject(this)
        return v
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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
            overScrollMode = View.OVER_SCROLL_NEVER
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

            adapter = CharacterAdapter(ArrayList(), picasso, host)
        }
        charactersLiveData?.observe(
            viewLifecycleOwner
        ) { list ->
            list?.let {
                (recyclerView?.adapter as CharacterAdapter).changeContacts(it)
                if (it.isNotEmpty()) {
                    showRecycler()
                } else showNoResults()
            }
        }
        initShowButtonListener()
        initUseFilterButtonListener()
    }

    override fun onDetach() {
        super.onDetach()
        hasBottomNavs = null
        host = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_search, menu)
        val menuItem = menu.findItem(R.id.search_menu_view)
        val searchView = menuItem.actionView as SearchView

        SearchViewUtil.fromView(searchView).debounce(200, TimeUnit.MILLISECONDS)
            .filter { t -> t.isNotBlank() && t.length >= 3 }
            .map { t -> t.trim() }
            .distinctUntilChanged()
            .switchMap { t -> Observable.just(t) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : Observer<String> {
                override fun onNext(t: String) {
                    Log.d("12356", "onNext: $t")
                    viewModel.makeQueryForSearchView(t)
                }

                override fun onSubscribe(d: Disposable) {
                    Log.d("12356", "onSubscribe")
                }

                override fun onError(e: Throwable) {
                    Log.d("12356", "onError: ${e.localizedMessage}")
                }

                override fun onComplete() {
                    Log.d("12356", "onComplete}")
                }

            })
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
                || gender != null
                || status != null
            ) {
                query = CharacterQuery(
                    name = name,
                    type = type,
                    species = species,
                    gender = gender?.name,
                    status = status?.name
                )
            }
            viewModel.getData(query)
        }
    }

    private fun showRecycler() {
        recyclerView?.visibility = View.VISIBLE
        noResults?.visibility = View.INVISIBLE
    }

    private fun showNoResults() {
        recyclerView?.visibility = View.INVISIBLE
        noResults?.visibility = View.VISIBLE
    }

    private fun initPullToRefresh(v: View) {
        val swipe: SwipeRefreshLayout = v.findViewById(R.id.swipe_layout)
        swipe.setOnRefreshListener {
            viewModel.getData(null)
            swipe.isRefreshing = false
        }
    }
}