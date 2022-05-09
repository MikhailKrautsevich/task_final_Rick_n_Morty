package com.mk.rickmortyappbykrautsevich.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mk.rickmortyappbykrautsevich.dataproviders.AllCharactersProvider
import com.mk.rickmortyappbykrautsevich.fragments.recyclers_data.CharacterRecData
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers

class AllCharactersViewModel : ViewModel() {

    private val dataProvider: AllCharactersProvider = AllCharactersProvider()
    private val listLiveData: MutableLiveData<List<CharacterRecData>> = MutableLiveData()
    private val loadingLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val paginationLiveData: MutableLiveData<Boolean> = MutableLiveData()

    private val charactersList: ArrayList<CharacterRecData> = ArrayList()
    private val compositeDisposable = CompositeDisposable()

    init {
        // при true ProgressBar виден
        loadingLiveData.postValue(true)
        val single = dataProvider.loadCharacters()
        single?.let {
            val disposable = it.observeOn(Schedulers.newThread())
                .subscribeWith(object : DisposableSingleObserver<List<CharacterRecData>>() {
                    override fun onSuccess(t: List<CharacterRecData>) {
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

    fun getCharactersList() = listLiveData as LiveData<List<CharacterRecData>>

    fun getMoreData() {
        if (dataProvider.hasMoreData()) {
            paginationLiveData.postValue(true)
            val single = dataProvider.loadNewPage()
            single?.let {
                val disposable = it.observeOn(Schedulers.newThread())
                    .subscribeWith(object : DisposableSingleObserver<List<CharacterRecData>>() {
                        override fun onSuccess(t: List<CharacterRecData>) {
                            addSynchronized(t)
                            paginationLiveData.postValue(false)
                        }

                        override fun onError(e: Throwable) {
                            paginationLiveData.postValue(false)
                        }

                    })
                compositeDisposable.add(disposable)
            }
        }
    }

    private fun postEmptyList() {
        loadingLiveData.postValue(false)
        listLiveData.postValue(ArrayList<CharacterRecData>() as List<CharacterRecData>)
    }

    @Synchronized
    private fun addSynchronized(list: List<CharacterRecData>) {
        charactersList.addAll(list)
        charactersList.sortBy { it.id }
        listLiveData.postValue(charactersList)
    }
}

