package com.mk.rickmortyappbykrautsevich.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mk.rickmortyappbykrautsevich.data.dataproviders.ListEpisodesProvider
import com.mk.rickmortyappbykrautsevich.data.dataproviders.interfaces.ListEpisodesProviderInterface
import com.mk.rickmortyappbykrautsevich.presentation.fragments.recyclers_data.EpisodeData
import com.mk.rickmortyappbykrautsevich.data.retrofit.models.queries.EpisodeQuery
import com.mk.rickmortyappbykrautsevich.presentation.viewmodels.interfaces.AllEpisodesViewModelInterface
import com.mk.rickmortyappbykrautsevich.presentation.viewmodels.interfaces.UsesPagination
import com.mk.rickmortyappbykrautsevich.presentation.viewmodels.interfaces.WithSearchView
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers

class AllEpisodesViewModel : ViewModel(), UsesPagination, WithSearchView,
    AllEpisodesViewModelInterface {
    private val dataProvider: ListEpisodesProviderInterface = ListEpisodesProvider()
    private val listLiveData: MutableLiveData<List<EpisodeData>> = MutableLiveData()
    private val loadingLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val paginationLiveData: MutableLiveData<Boolean> = MutableLiveData()

    private var episodesList: ArrayList<EpisodeData> = ArrayList()
    private val compositeDisposable = CompositeDisposable()

    private var currentQuery: EpisodeQuery? = null

    init {
        getData(null)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    override fun getLoadingLiveData() = loadingLiveData as LiveData<Boolean>

    override fun getPaginationLiveData() = paginationLiveData as LiveData<Boolean>

    override fun getEpisodesList() = listLiveData as LiveData<List<EpisodeData>>

    override fun getData(query: EpisodeQuery?) {
        loadingLiveData.postValue(true)
        currentQuery = query
        episodesList = ArrayList()
        val single = dataProvider.loadEpisodes(query)
        single?.let {
            val disposable =
                it.observeOn(Schedulers.newThread())
                    .subscribeWith(object : DisposableSingleObserver<List<EpisodeData>>() {
                        override fun onSuccess(t: List<EpisodeData>) {
                            addSynchronized(t)
                            loadingLiveData.postValue(false)
                        }

                        override fun onError(e: Throwable) {
                            postEmptyList()
                            loadingLiveData.postValue(false)
                        }

                    })
            compositeDisposable.add(disposable)
        } ?: postEmptyList()
    }

    override fun getMoreData() {
        if (dataProvider.hasMoreData()) {
            paginationLiveData.postValue(true)
            val single = dataProvider.loadNewPage()
            single?.let {
                val disposable = it.observeOn(Schedulers.newThread())
                    .subscribeWith(object : DisposableSingleObserver<List<EpisodeData>>() {
                        override fun onSuccess(t: List<EpisodeData>) {
                            addSynchronized(t)
                            paginationLiveData.postValue(false)
                        }

                        override fun onError(e: Throwable) {
                            Log.d(
                                "12356",
                                "AllEpisodesViewModel: getMoreData() onError() ${e.localizedMessage}"
                            )
                            paginationLiveData.postValue(false)
                        }

                    })
                compositeDisposable.add(disposable)
            }
        }
    }

    private fun postEmptyList() {
        loadingLiveData.postValue(false)
        listLiveData.postValue(ArrayList<EpisodeData>() as List<EpisodeData>)
    }

    @Synchronized
    private fun addSynchronized(list: List<EpisodeData>) {
        episodesList.addAll(list)
        episodesList.sortBy { it.id }
        listLiveData.postValue(episodesList)
    }

    override fun makeQueryForSearchView(s: String?) {
        val query = EpisodeQuery(s, "")
        getData(query)
    }
}