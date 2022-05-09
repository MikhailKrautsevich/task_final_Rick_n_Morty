package com.mk.rickmortyappbykrautsevich.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mk.rickmortyappbykrautsevich.dataproviders.AllEpisodeProvider
import com.mk.rickmortyappbykrautsevich.fragments.recyclers_data.EpisodeRecData
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers

class AllEpisodesViewModel : ViewModel() {
    private val dataProvider: AllEpisodeProvider = AllEpisodeProvider()
    private val listLiveData: MutableLiveData<List<EpisodeRecData>> = MutableLiveData()
    private val loadingLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val paginationLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val hasNextPageLiveData: MutableLiveData<Boolean> = MutableLiveData()

    private val episodesList: ArrayList<EpisodeRecData> = ArrayList()
    private val compositeDisposable = CompositeDisposable()

    init {
        // при true ProgressBar виден
        loadingLiveData.postValue(true)
        val single = dataProvider.loadEpisodes()
        single?.let {
            val disposable =
                it.observeOn(Schedulers.newThread())
                    .subscribeWith(object : DisposableSingleObserver<List<EpisodeRecData>>() {
                        override fun onSuccess(t: List<EpisodeRecData>) {
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

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun getLoadingLiveData() = loadingLiveData as LiveData<Boolean>

    fun getPaginationLiveData() = paginationLiveData as LiveData<Boolean>

    fun getHasNextPageLiveData() = hasNextPageLiveData as LiveData<Boolean>

    fun getEpisodesList() = listLiveData as LiveData<List<EpisodeRecData>>

    fun getMoreData() {
        if (dataProvider.hasMoreData()) {
            paginationLiveData.postValue(true)
            val single = dataProvider.loadNewPage()
            single?.let {
                val disposable = it.observeOn(Schedulers.newThread())
                    .subscribeWith(object : DisposableSingleObserver<List<EpisodeRecData>>() {
                        override fun onSuccess(t: List<EpisodeRecData>) {
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
        } else hasNextPageLiveData.postValue(false)
    }

    private fun postEmptyList() {
        loadingLiveData.postValue(false)
        listLiveData.postValue(ArrayList<EpisodeRecData>() as List<EpisodeRecData>)
    }

    @Synchronized
    private fun addSynchronized(list: List<EpisodeRecData>) {
        episodesList.addAll(list)
        episodesList.sortBy { it.id }
        listLiveData.postValue(episodesList)
    }
}