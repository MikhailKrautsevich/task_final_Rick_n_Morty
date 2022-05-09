package com.mk.rickmortyappbykrautsevich.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mk.rickmortyappbykrautsevich.dataproviders.AllLocationsProvider
import com.mk.rickmortyappbykrautsevich.fragments.recyclers_data.LocationRecData
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers

class AllLocationViewModel : ViewModel() {
    private val dataProvider: AllLocationsProvider = AllLocationsProvider()
    private val listLiveData: MutableLiveData<List<LocationRecData>> = MutableLiveData()
    private val loadingLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val paginationLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val hasNextPageLiveData: MutableLiveData<Boolean> = MutableLiveData()

    private val locationsList: ArrayList<LocationRecData> = ArrayList()
    private val compositeDisposable = CompositeDisposable()

    init {
        // при true ProgressBar виден
        loadingLiveData.postValue(true)
        val single = dataProvider.loadAllLocations()
        single?.let {
            val disposable = it.observeOn(Schedulers.newThread())
                .subscribeWith(object : DisposableSingleObserver<List<LocationRecData>>() {
                    override fun onSuccess(t: List<LocationRecData>) {
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

    fun getLocationsList() = listLiveData as LiveData<List<LocationRecData>>

    fun getMoreData() {
        if (dataProvider.hasMoreData()) {
            paginationLiveData.postValue(true)
            val single = dataProvider.loadNewPage()
            single?.let {
                val disposable = it.observeOn(Schedulers.newThread())
                    .subscribeWith(object : DisposableSingleObserver<List<LocationRecData>>() {
                        override fun onSuccess(t: List<LocationRecData>) {
                            addSynchronized(t)
                            paginationLiveData.postValue(false)
                        }

                        override fun onError(e: Throwable) {
                            Log.d(
                                "12356",
                                "AllLocationViewModel: getMoreData() onError() ${e.localizedMessage}"
                            )
                        }

                    })
                compositeDisposable.add(disposable)
            }
        } else hasNextPageLiveData.postValue(false)
    }

    private fun postEmptyList() {
        loadingLiveData.postValue(false)
        listLiveData.postValue(ArrayList<LocationRecData>() as List<LocationRecData>)
    }

    @Synchronized
    private fun addSynchronized(list: List<LocationRecData>) {
        locationsList.addAll(list)
        listLiveData.postValue(locationsList)
    }
}