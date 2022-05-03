package com.mk.rickmortyappbykrautsevich.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mk.rickmortyappbykrautsevich.dataproviders.AllEpisodeProvider
import com.mk.rickmortyappbykrautsevich.fragments.recyclers_data.EpisodeRecData
import com.mk.rickmortyappbykrautsevich.retrofit.models.EpisodeRetrofitModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers

class AllEpisodesViewModel : ViewModel() {
    private val dataProvider: AllEpisodeProvider = AllEpisodeProvider()
    private val listLiveData: MutableLiveData<List<EpisodeRecData>> = MutableLiveData()
    private val loadingLiveData: MutableLiveData<Boolean> = MutableLiveData()

    //    private val episodesList: ArrayList<EpisodesRecData> = ArrayList()
    private var disposable: Disposable? = null

    init {
        // при true ProgressBar виден
        loadingLiveData.postValue(true)
        val single = dataProvider.loadAllEpisodes()
        single?.let {
            disposable = it.subscribeOn(Schedulers.io()).flatMap { t -> Single.just(t.results) }
                .flatMap { t ->
                    Single.just(
                        transformRepoEpsListInRecEpsList(t)
                    )
                }.subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<List<EpisodeRecData>>() {
                    override fun onSuccess(t: List<EpisodeRecData>) {
                        listLiveData.postValue(t)
                        loadingLiveData.postValue(false)
                    }

                    override fun onError(e: Throwable) {
                        postEmptyList()
                        loadingLiveData.postValue(false)
                    }

                })
        } ?: postEmptyList()
    }

    private fun transformRepoEpsListInRecEpsList(list: List<EpisodeRetrofitModel>?): List<EpisodeRecData> {
        val l = ArrayList<EpisodeRecData>()
        list?.forEach { l.add(EpisodeRecData(it)) }
        return l
    }

    override fun onCleared() {
        super.onCleared()
        disposable?.dispose()
    }

    fun getLoadingLiveData() = loadingLiveData as LiveData<Boolean>

    fun getEpisodesList() = listLiveData as LiveData<List<EpisodeRecData>>

    private fun postEmptyList() {
        loadingLiveData.postValue(false)
        listLiveData.postValue(ArrayList<EpisodeRecData>() as List<EpisodeRecData>)
    }
}