package com.mk.rickmortyappbykrautsevich.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mk.rickmortyappbykrautsevich.dataproviders.LocationDetailProvider
import com.mk.rickmortyappbykrautsevich.fragments.recyclers_data.CharacterData
import com.mk.rickmortyappbykrautsevich.fragments.recyclers_data.LocationData
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers

class LocationDetailViewModel : ViewModel() {
    private val dataProvider: LocationDetailProvider = LocationDetailProvider()

    private val locLiveData: MutableLiveData<LocationData> = MutableLiveData()
    private val loadingLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val listLoadingLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val listLiveData: MutableLiveData<List<CharacterData>> = MutableLiveData()

    private val compositeDisposable = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun getLocLoadingLiveData() = loadingLiveData as LiveData<Boolean>

    fun getLocationLiveData() = locLiveData as LiveData<LocationData>

    fun getListLoadingLiveData() = listLoadingLiveData as LiveData<Boolean>

    fun getListLiveData() = listLiveData as LiveData<List<CharacterData>>

    fun loadData(id: Int) {
        loadingLiveData.postValue(true)
        val single = dataProvider.loadData(id)
        single?.let {
            val disposable = it.observeOn(Schedulers.newThread())
                .subscribeWith(object : DisposableSingleObserver<LocationData>() {
                    override fun onSuccess(t: LocationData) {
                        locLiveData.postValue(t)
                        loadingLiveData.postValue(false)
                    }

                    override fun onError(e: Throwable) {
                        postEmptyData()
                    }
                })
            compositeDisposable.add(disposable)
        } ?: postEmptyData()
    }

    fun loadList(list: List<String>?) {
        listLoadingLiveData.postValue(true)
        list?.let {
            if (it.isNotEmpty()) {
                val list1 = dataProvider.loadList(list)
                compositeDisposable.add(list1.observeOn(Schedulers.newThread())
                    .subscribeWith(object : DisposableSingleObserver<List<CharacterData>>() {
                        override fun onError(e: Throwable) {
                            listLoadingLiveData.postValue(false)
                            e.localizedMessage?.let { it1 -> Log.d("112211", it1) }
                        }

                        override fun onSuccess(t: List<CharacterData>) {
                            listLoadingLiveData.postValue(false)
                            listLiveData.postValue(t)
                        }
                    }
                    )
                )
            } else listLoadingLiveData.postValue(false)
        } ?: listLoadingLiveData.postValue(false)
    }

    // Метод для обработки ошибок, метод нужно вызвать при полном отсутствии данных.
    private fun postEmptyData() {
        loadingLiveData.postValue(false)
        val empty = LocationData()
        locLiveData.postValue(empty)
    }
}