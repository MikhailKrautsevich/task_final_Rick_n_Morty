package com.mk.rickmortyappbykrautsevich.data.dataproviders

import android.util.Log
import com.mk.rickmortyappbykrautsevich.App
import com.mk.rickmortyappbykrautsevich.data.db.entities.CharacterEntity
import com.mk.rickmortyappbykrautsevich.presentation.fragments.recyclers_data.CharacterData
import com.mk.rickmortyappbykrautsevich.data.retrofit.RetrofitHelper
import com.mk.rickmortyappbykrautsevich.data.retrofit.api.GetCharactersApi
import com.mk.rickmortyappbykrautsevich.data.retrofit.models.AllCharactersContainer
import com.mk.rickmortyappbykrautsevich.data.retrofit.models.CharacterRetrofitModel
import com.mk.rickmortyappbykrautsevich.data.retrofit.models.queries.CharacterQuery
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

class ListCharactersProvider {

    private var api: GetCharactersApi? = null
    private var currentPageNumber = 1

    // при запросе по умолчанию число страниц 42
    private var maxPageNumber = 42
    private var currentQuery: CharacterQuery? = null

    private val app = App.instance
    private val dao = app?.getDataBase()?.getCharacterDao()

    init {
        val retrofit = RetrofitHelper.getRetrofit(RetrofitHelper.getOkHttpClient())
        api = RetrofitHelper.getCharsApi(retrofit)
    }

    fun loadCharacters(query: CharacterQuery?): Single<List<CharacterData>>? {
        val isNetworkAvailable = app!!.isNetworkAvailable()
        if (isNetworkAvailable) {
            currentPageNumber = 1
            currentQuery = query
            val single: Single<AllCharactersContainer>? = if (query == null) {
                api?.getCharacters()
            } else api?.getCharacters(
                name = query.name,
                status = query.status,
                species = query.species,
                type = query.type,
                gender = query.gender
            )
            return handleSingle(single)
        } else {
            // отключаем пагинацию
            maxPageNumber = -1
            val fromCash = if (query == null) {
                dao!!.getAllCharacters()
            } else dao!!.getCharacters(
                "%${query.name}%",
                "%${query.species}%",
                "%${query.type}%",
                query.gender,
                query.status
            )
            return fromCash.subscribeOn(Schedulers.io())
                .toObservable()
                .flatMap { t ->
                    Log.d("1222", t.toString())
                    Observable.fromIterable(t)
                }
                .map { t ->
                    Log.d("1222", t.toString())
                    CharacterData(t)
                }
                .toList()
        }
    }

    fun loadNewPage(): Single<List<CharacterData>>? {
        return if (hasMoreData()) {
            val single = api?.getCharacters(
                page = ++currentPageNumber,
                name = currentQuery?.name,
                status = currentQuery?.status,
                species = currentQuery?.species,
                type = currentQuery?.type,
                gender = currentQuery?.gender
            )
            handleSingle(single)
        } else Single.just(emptyList())
    }

    private fun handleSingle(single: Single<AllCharactersContainer>?): Single<List<CharacterData>>? {
        val result: Single<List<CharacterData>>? =
            single?.subscribeOn(Schedulers.io())?.map { t ->
                getMaxPage(t)
                t.results
            }
                ?.map { t ->
                    saveListToBD(t)
                    t
                }
                ?.map { t ->
                    transformRepoCharsListInRecCharsList(t)
                }?.subscribeOn(Schedulers.computation())
        return result
    }

    private fun transformRepoCharsListInRecCharsList(list: List<CharacterRetrofitModel>): List<CharacterData> {
        val l = ArrayList<CharacterData>()
        list.forEach { l.add(CharacterData(it)) }
        return l
    }

    fun hasMoreData(): Boolean = currentPageNumber + 1 <= maxPageNumber

    private fun getMaxPage(container: AllCharactersContainer) {
        maxPageNumber = container.info.pages
    }

    private fun saveListToBD(listLRM: List<CharacterRetrofitModel>?) {
        if (listLRM != null) {
            Flowable.fromIterable(listLRM)
                .map { t -> transformRepoCharInEntity(t) }
                .toList()
                .observeOn(Schedulers.newThread())
                .subscribeWith(object : SingleObserver<List<CharacterEntity>> {
                    override fun onSubscribe(d: Disposable) {
                        Log.d("112211", "AllLocationProvider: onSubscribe")
                    }

                    override fun onSuccess(t: List<CharacterEntity>) {
                        Log.d("112211", "AllLocationProvider: onSuccess - start")
                        dao?.insertList(t)
                        Log.d("112211", "AllLocationProvider: onSuccess - finish")
                    }

                    override fun onError(e: Throwable) {
                        Log.d("112211", "AllLocationProvider: onError ${e.localizedMessage}")
                    }
                })
        }
    }

    private fun transformRepoCharInEntity(model: CharacterRetrofitModel): CharacterEntity {
        return CharacterEntity(model)
    }
}