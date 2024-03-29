package com.mk.rickmortyappbykrautsevich.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mk.rickmortyappbykrautsevich.data.dataproviders.ListCharactersProvider
import com.mk.rickmortyappbykrautsevich.data.dataproviders.interfaces.ListCharactersProviderInterface
import com.mk.rickmortyappbykrautsevich.presentation.fragments.recyclers_data.CharacterData
import com.mk.rickmortyappbykrautsevich.data.retrofit.models.queries.CharacterQuery
import com.mk.rickmortyappbykrautsevich.presentation.viewmodels.interfaces.AllCharactersViewModelInterface
import com.mk.rickmortyappbykrautsevich.presentation.viewmodels.interfaces.UsesPagination
import com.mk.rickmortyappbykrautsevich.presentation.viewmodels.interfaces.WithSearchView
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers

class AllCharactersViewModel : ViewModel(), UsesPagination, WithSearchView,
    AllCharactersViewModelInterface {

    private val dataProvider: ListCharactersProviderInterface = ListCharactersProvider()
    private val listLiveData: MutableLiveData<List<CharacterData>> = MutableLiveData()
    private val loadingLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val paginationLiveData: MutableLiveData<Boolean> = MutableLiveData()

    private var charactersList: ArrayList<CharacterData> = ArrayList()
    private val compositeDisposable = CompositeDisposable()

    private var currentQuery: CharacterQuery? = null

    init {
        getData(null)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    override fun getLoadingLiveData() = loadingLiveData as LiveData<Boolean>

    override fun getPaginationLiveData() = paginationLiveData as LiveData<Boolean>

    override fun getCharactersList() = listLiveData as LiveData<List<CharacterData>>

    override fun getData(query: CharacterQuery?) {
        // при true ProgressBar виден
        loadingLiveData.postValue(true)
        currentQuery = query
        charactersList = ArrayList()
        val single = dataProvider.loadCharacters(query)
        single?.let {
            val disposable = it.observeOn(Schedulers.newThread())
                .subscribeWith(object : DisposableSingleObserver<List<CharacterData>>() {
                    override fun onSuccess(t: List<CharacterData>) {
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
                    .subscribeWith(object : DisposableSingleObserver<List<CharacterData>>() {
                        override fun onSuccess(t: List<CharacterData>) {
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
        listLiveData.postValue(ArrayList<CharacterData>() as List<CharacterData>)
    }

    @Synchronized
    private fun addSynchronized(list: List<CharacterData>) {
        charactersList.addAll(list)
        charactersList.sortBy { it.id }
        listLiveData.postValue(charactersList)
    }

    override fun makeQueryForSearchView(s: String?) {
        val query = CharacterQuery(s, "", "", "", "")
        getData(query)
    }
}

