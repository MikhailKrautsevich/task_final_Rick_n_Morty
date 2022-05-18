package com.mk.rickmortyappbykrautsevich.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mk.rickmortyappbykrautsevich.data.dataproviders.LocationDetailProvider
import com.mk.rickmortyappbykrautsevich.data.dataproviders.interfaces.LocationDetailProviderInterface
import com.mk.rickmortyappbykrautsevich.presentation.fragments.recyclers_data.CharacterData
import com.mk.rickmortyappbykrautsevich.presentation.fragments.recyclers_data.LocationData
import com.mk.rickmortyappbykrautsevich.presentation.viewmodels.interfaces.LocationDetailViewModelInterface
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers

class LocationDetailViewModel : ViewModel(), LocationDetailViewModelInterface {
    private val dataProvider: LocationDetailProviderInterface = LocationDetailProvider()

    private val locLiveData: MutableLiveData<LocationData> = MutableLiveData()
    private val loadingLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val listLoadingLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val listLiveData: MutableLiveData<List<CharacterData>> = MutableLiveData()

    private val compositeDisposable = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    override fun getLocLoadingLiveData() = loadingLiveData as LiveData<Boolean>

    override fun getLocationLiveData() = locLiveData as LiveData<LocationData>

    override fun getListLoadingLiveData() = listLoadingLiveData as LiveData<Boolean>

    override fun getListLiveData() = listLiveData as LiveData<List<CharacterData>>

    override fun loadData(id: Int) {
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

    override fun loadList(list: List<String>?) {
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