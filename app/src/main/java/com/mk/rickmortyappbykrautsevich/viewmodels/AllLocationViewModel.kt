package com.mk.rickmortyappbykrautsevich.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mk.rickmortyappbykrautsevich.dataproviders.AllLocationsProvider
import com.mk.rickmortyappbykrautsevich.fragments.recyclers_data.LocationRecData
import com.mk.rickmortyappbykrautsevich.retrofit.models.LocationRetrofitModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers

class AllLocationViewModel : ViewModel() {
    private val dataProvider: AllLocationsProvider = AllLocationsProvider()
    private val listLiveData: MutableLiveData<List<LocationRecData>> = MutableLiveData()
    private val loadingLiveData: MutableLiveData<Boolean> = MutableLiveData()

    //    private val locationsList: ArrayList<LocationRecData> = ArrayList()
    private var disposable: Disposable? = null

    init {
        // при true ProgressBar виден
        loadingLiveData.postValue(true)
        val single = dataProvider.loadAllLocations()
        single?.let {
            disposable = it.subscribeOn(Schedulers.io()).flatMap { t -> Single.just(t.results) }
                .flatMap { t ->
                    Single.just(
                        transformRepoLocsListInRecLocsList(t)
                    )
                }.subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<List<LocationRecData>>() {
                    override fun onSuccess(t: List<LocationRecData>) {
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

    private fun transformRepoLocsListInRecLocsList(list: List<LocationRetrofitModel>?): List<LocationRecData> {
        val l = ArrayList<LocationRecData>()
        list?.forEach { l.add(LocationRecData(it)) }
        return l
    }

    override fun onCleared() {
        super.onCleared()
        disposable?.dispose()
    }

    fun getLoadingLiveData() = loadingLiveData as LiveData<Boolean>

    fun getLocationsList() = listLiveData as LiveData<List<LocationRecData>>

    private fun postEmptyList() {
        loadingLiveData.postValue(false)
        listLiveData.postValue(ArrayList<LocationRecData>() as List<LocationRecData>)
    }
}