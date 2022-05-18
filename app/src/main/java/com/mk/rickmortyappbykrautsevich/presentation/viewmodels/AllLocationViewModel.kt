package com.mk.rickmortyappbykrautsevich.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mk.rickmortyappbykrautsevich.data.dataproviders.ListLocationsProvider
import com.mk.rickmortyappbykrautsevich.data.dataproviders.interfaces.ListLocationsProviderInterface
import com.mk.rickmortyappbykrautsevich.presentation.fragments.recyclers_data.LocationData
import com.mk.rickmortyappbykrautsevich.data.retrofit.models.queries.LocationQuery
import com.mk.rickmortyappbykrautsevich.presentation.viewmodels.interfaces.AllLocationViewModelInterface
import com.mk.rickmortyappbykrautsevich.presentation.viewmodels.interfaces.UsesPagination
import com.mk.rickmortyappbykrautsevich.presentation.viewmodels.interfaces.WithSearchView
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers

class AllLocationViewModel : ViewModel(), UsesPagination, WithSearchView,
    AllLocationViewModelInterface {
    private val dataProvider: ListLocationsProviderInterface = ListLocationsProvider()
    private val listLiveData: MutableLiveData<List<LocationData>> = MutableLiveData()
    private val loadingLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val paginationLiveData: MutableLiveData<Boolean> = MutableLiveData()

    private var locationsList: ArrayList<LocationData> = ArrayList()
    private val compositeDisposable = CompositeDisposable()

    private var currentQuery: LocationQuery? = null

    init {
        getData(null)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    override fun getLoadingLiveData() = loadingLiveData as LiveData<Boolean>

    override fun getPaginationLiveData() = paginationLiveData as LiveData<Boolean>

    override fun getLocationsList() = listLiveData as LiveData<List<LocationData>>

    override fun getData(query: LocationQuery?) {
        // при true ProgressBar виден
        loadingLiveData.postValue(true)
        currentQuery = query
        locationsList = ArrayList()
        val single = dataProvider.loadLocations(query)
        single?.let {
            val disposable = it.observeOn(Schedulers.newThread())
                .subscribeWith(object : DisposableSingleObserver<List<LocationData>>() {
                    override fun onSuccess(t: List<LocationData>) {
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
                    .subscribeWith(object : DisposableSingleObserver<List<LocationData>>() {
                        override fun onSuccess(t: List<LocationData>) {
                            addSynchronized(t)
                            paginationLiveData.postValue(false)
                        }

                        override fun onError(e: Throwable) {
                            Log.d(
                                "12356",
                                "AllLocationViewModel: getMoreData() onError() ${e.localizedMessage}"
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
        listLiveData.postValue(ArrayList<LocationData>() as List<LocationData>)
    }

    @Synchronized
    private fun addSynchronized(list: List<LocationData>) {
        locationsList.addAll(list)
        locationsList.sortBy { it.id }
        listLiveData.postValue(locationsList)
    }

    override fun makeQueryForSearchView(s: String?) {
        val query = LocationQuery(s, "", "")
        getData(query)
    }
}