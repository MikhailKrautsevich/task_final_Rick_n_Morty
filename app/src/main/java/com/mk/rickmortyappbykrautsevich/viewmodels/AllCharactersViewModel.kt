package com.mk.rickmortyappbykrautsevich.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mk.rickmortyappbykrautsevich.dataproviders.AllCharactersProvider
import com.mk.rickmortyappbykrautsevich.fragments.recyclers_data.CharacterRecData
import com.mk.rickmortyappbykrautsevich.retrofit.models.CharacterRetrofitModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers

class AllCharactersViewModel : ViewModel() {

//    companion object {
//        const val LOG = "RickMorty"
//    }

    private val dataProvider: AllCharactersProvider = AllCharactersProvider()
    private val listLiveData: MutableLiveData<List<CharacterRecData>> = MutableLiveData()
    private val loadingLiveData: MutableLiveData<Boolean> = MutableLiveData()

//    private val charactersList: ArrayList<CharacterRecData> = ArrayList()
    private var disposable: Disposable? = null

    init {
        // при true ProgressBar виден
        loadingLiveData.postValue(true)
        val single = dataProvider.loadAllCharacters()
        single?.let {
            disposable = it.subscribeOn(Schedulers.io()).flatMap { t -> Single.just(t.results) }
                .flatMap { t ->
                    Single.just(
                        transformRepoCharsListInRecCharsList(t)
                    )
                }.subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<List<CharacterRecData>>() {
                    override fun onSuccess(t: List<CharacterRecData>) {
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

    override fun onCleared() {
        super.onCleared()
        disposable?.dispose()
    }

    fun getLoadingLiveData() = loadingLiveData as LiveData<Boolean>

    fun getCharactersList() = listLiveData as LiveData<List<CharacterRecData>>

    private fun postEmptyList() {
        loadingLiveData.postValue(false)
        listLiveData.postValue(ArrayList<CharacterRecData>() as List<CharacterRecData>)
    }

    private fun transformRepoCharsListInRecCharsList(list: List<CharacterRetrofitModel>): List<CharacterRecData> {
        val l = ArrayList<CharacterRecData>()
        list.forEach { l.add(CharacterRecData(it)) }
        return l
    }
}

