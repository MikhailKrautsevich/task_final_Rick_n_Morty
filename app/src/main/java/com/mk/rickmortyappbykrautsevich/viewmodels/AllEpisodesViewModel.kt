package com.mk.rickmortyappbykrautsevich.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mk.rickmortyappbykrautsevich.dataproviders.AllEpisodeProvider
import com.mk.rickmortyappbykrautsevich.fragments.recyclers_data.EpisodeData
import com.mk.rickmortyappbykrautsevich.retrofit.models.queries.EpisodeQuery
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers

class AllEpisodesViewModel : ViewModel() {
    private val dataProvider: AllEpisodeProvider = AllEpisodeProvider()
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

    fun getLoadingLiveData() = loadingLiveData as LiveData<Boolean>

    fun getPaginationLiveData() = paginationLiveData as LiveData<Boolean>

    fun getEpisodesList() = listLiveData as LiveData<List<EpisodeData>>

    fun getData(query: EpisodeQuery?) {
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

    fun getMoreData() {
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
}